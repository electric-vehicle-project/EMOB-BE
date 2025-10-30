/* EMOB-2025 */
package com.example.emob.service;

import com.example.emob.constant.*;
import com.example.emob.entity.*;
import com.example.emob.exception.GlobalException;
import com.example.emob.mapper.PageMapper;
import com.example.emob.mapper.SaleOrderMapper;
import com.example.emob.model.request.SaleOrderItemRequest;
import com.example.emob.model.response.APIResponse;
import com.example.emob.model.response.PageResponse;
import com.example.emob.model.response.SaleOrder.SaleOrderResponse;
import com.example.emob.model.response.SalesByStaffResponse;
import com.example.emob.repository.*;
import com.example.emob.service.impl.ISaleOrder;
import com.example.emob.util.AccountUtil;
import com.example.emob.util.PromotionHelper;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class SaleOrderService implements ISaleOrder {
  @Autowired private SaleOrderRepository saleOrderRepository;

  @Autowired private PromotionRepository promotionRepository;
  @Autowired private SaleOrderMapper saleOrderMapper;
  @Autowired private PageMapper pageMapper;
  @Autowired private InstallmentPlanService installmentPlanService;
  @Autowired private VehicleUnitRepository vehicleUnitRepository;
  @Autowired private CustomerRepository customerRepository;
  @Autowired private ContractService contractService;
  @Autowired private AccountRepository accountRepository;

  @Override
  @Transactional
  public APIResponse<SaleOrderResponse> createSaleOrderFromQuotation(
      Quotation quotation, List<SaleOrderItemRequest> itemRequests) {

    Set<UUID> quotationItemIds =
        itemRequests.stream().map(SaleOrderItemRequest::getItemsId).collect(Collectors.toSet());

    // === 1. Duyệt các QuotationItem được chọn ===
    Set<SaleOrderItem> saleOrderItems =
        quotation.getQuotationItems().stream()
            .filter(item -> quotationItemIds.contains(item.getId()))
            .map(
                item -> {
                  SaleOrderItemRequest req =
                      itemRequests.stream()
                          .filter(r -> r.getItemsId().equals(item.getId()))
                          .findFirst()
                          .orElseThrow(
                              () ->
                                  new GlobalException(
                                      ErrorCode.INVALID_CODE, "Invalid item request"));

                  Promotion promotion = null;
                  if (req.getPromotionId() != null) {
                    promotion =
                        promotionRepository
                            .findById(req.getPromotionId())
                            .orElseThrow(
                                () ->
                                    new GlobalException(
                                        ErrorCode.NOT_FOUND, "Promotion not found"));
                    PromotionHelper.checkPromotionExists(promotion, item.getVehicle());
                    PromotionHelper.checkPromotionValid(promotion);
                  }

                  // === 2. Tạo SaleOrderItem ===
                  SaleOrderItem saleItem = new SaleOrderItem();

                  // === 3. Gán các xe khả dụng ===
                  List<VehicleUnit> units =
                      vehicleUnitRepository
                          .findTopNByInventoryAndVehicleAndColorIgnoreCaseAndStatus(
                              AccountUtil.getCurrentUser().getDealer().getInventory(),
                              item.getVehicle(),
                              item.getColor(),
                              item.getVehicleStatus(),
                              PageRequest.of(0, req.getQuantity()));

                  saleItem.setVehicleUnits(new HashSet<>(units));

                  BigDecimal unitPrice =
                      Objects.requireNonNullElse(item.getUnitPrice(), BigDecimal.ZERO);
                  BigDecimal discountedPrice =
                      PromotionHelper.calculateDiscountedPrice(
                          unitPrice, promotion, quotation.getCustomer());
                  BigDecimal totalPrice =
                      discountedPrice.multiply(BigDecimal.valueOf(item.getQuantity()));

                  saleItem.setQuantity(item.getQuantity());
                  saleItem.setVehicleStatus(item.getVehicleStatus());
                  saleItem.setPromotion(promotion);
                  saleItem.setColor(item.getColor());
                  saleItem.setUnitPrice(unitPrice);
                  saleItem.setDiscountPrice(discountedPrice);
                  saleItem.setTotalPrice(totalPrice);

                  return saleItem;
                })
            .collect(Collectors.toSet());

    // === 4. Tạo SaleOrder ===
    SaleOrder saleOrder = new SaleOrder();
    saleOrder.setAccount(AccountUtil.getCurrentUser());
    saleOrder.setStatus(OrderStatus.CREATED);
    saleOrder.setCreatedAt(LocalDateTime.now());
    saleOrder.setQuotation(quotation);
    quotation.setSaleOrder(saleOrder);

    saleOrder.setSaleOrderItems(saleOrderItems);
    // === 5. Tính tổng tiền + VAT ===
    BigDecimal totalAmount =
        saleOrderItems.stream()
            .map(SaleOrderItem::getTotalPrice)
            .filter(Objects::nonNull)
            .reduce(BigDecimal.ZERO, BigDecimal::add);

    int totalQuantity =
        saleOrderItems.stream().mapToInt(SaleOrderItem::getQuantity).reduce(0, Integer::sum);

    BigDecimal vatRate = new BigDecimal("0.1");
    BigDecimal vatAmount = totalAmount.multiply(vatRate);
    BigDecimal totalWithVat = totalAmount.add(vatAmount);

    saleOrder.setVatAmount(vatAmount);
    saleOrder.setTotalPrice(totalWithVat);
    saleOrder.setTotalQuantity(totalQuantity);
    saleOrderItems.stream()
        .flatMap(item -> item.getVehicleUnits().stream().peek(unit -> unit.setSaleOrderItem(item)))
        .forEach(vehicleUnitRepository::save);

    // === 6. Persist toàn bộ theo cascade ===
    saleOrderItems.forEach(item -> item.setSaleOrder(saleOrder));
    SaleOrder savedSaleOrder = saleOrderRepository.save(saleOrder);

    SaleOrderResponse response = saleOrderMapper.toSaleOrderResponse(savedSaleOrder);
    return APIResponse.success(response, "Sale order created successfully");
  }

  @Override
  @Transactional
  public APIResponse<SaleOrderResponse> createSaleOrderFromVehicleRequest(
      VehicleRequest vehicleRequest) {
    SaleOrder saleOrder = saleOrderMapper.toSaleOrderFromVehicleRequest(vehicleRequest);
    Set<SaleOrderItem> saleOrderItems =
        vehicleRequest.getVehicleRequestItems().stream()
            .map(
                reqItem -> {
                  SaleOrderItem saleItem = new SaleOrderItem();
                  saleItem.setColor(reqItem.getColor());
                  saleItem.setVehicle(reqItem.getVehicle());
                  saleItem.setQuantity(reqItem.getQuantity());
                  saleItem.setUnitPrice(reqItem.getUnitPrice());
                  saleItem.setVehicleStatus(reqItem.getVehicleStatus());
                  saleItem.setTotalPrice(
                      Objects.requireNonNullElse(reqItem.getUnitPrice(), BigDecimal.ZERO)
                          .multiply(BigDecimal.valueOf(reqItem.getQuantity())));
                  saleItem.setSaleOrder(saleOrder);
                  return saleItem;
                })
            .collect(Collectors.toSet());

    saleOrder.setSaleOrderItems(saleOrderItems);
    saleOrder.setVehicleRequest(vehicleRequest);
    saleOrder.setCreatedAt(LocalDateTime.now());
    saleOrder.setAccount(AccountUtil.getCurrentUser());
    vehicleRequest.setSaleOrder(saleOrder);
    saleOrder.setStatus(OrderStatus.CREATED);
    // ✅ Lưu vào DB
    SaleOrder savedSaleOrder = saleOrderRepository.save(saleOrder);

    // ✅ Tạo response
    SaleOrderResponse response = saleOrderMapper.toSaleOrderResponse(savedSaleOrder);
    return APIResponse.success(response, "Sale order created successfully");
  }

  @Override
  @PreAuthorize("hasAnyRole('DEALER_STAFF', 'EVM_STAFF')")
  public APIResponse<SaleOrderResponse> deleteSaleOrderById(UUID saleOrderId) {
    SaleOrder saleOrder =
        saleOrderRepository
            .findById(saleOrderId)
            .orElseThrow(() -> new GlobalException(ErrorCode.NOT_FOUND, "Sale order not found"));
    saleOrder.setStatus(OrderStatus.CANCELED);
    saleOrder
        .getSaleOrderItems()
        .forEach(
            item -> {
              if (item.getVehicleUnits() != null) {
                item.getVehicleUnits().forEach(unit -> unit.setStatus(item.getVehicleStatus()));
              }
            });
    return APIResponse.success(
        saleOrderMapper.toSaleOrderResponse(saleOrderRepository.save(saleOrder)),
        "Sale order canceled successfully");
  }

  @Override
  @PreAuthorize("hasAnyRole('DEALER_STAFF', 'EVM_STAFF')")
  @Transactional
  public APIResponse<SaleOrderResponse> completeSaleOrderById(UUID id) {
    SaleOrder saleOrder =
        saleOrderRepository
            .findById(id)
            .orElseThrow(() -> new GlobalException(ErrorCode.NOT_FOUND, "Sale order not found"));
    if (saleOrder.getAccount().getRole().equals(Role.DEALER_STAFF)) {
      if (AccountUtil.getCurrentUser() != saleOrder.getAccount()) {
        throw new GlobalException(
            ErrorCode.UNAUTHENTICATED, "You are not allowed to approve this quotation");
      }
    }

    saleOrder.setStatus(OrderStatus.COMPLETED);
    // tạo hợp đồng bán hàng ở đây
    contractService.createContract(saleOrder);
    saleOrderRepository.save(saleOrder);
    return APIResponse.success(
        saleOrderMapper.toSaleOrderResponse(saleOrderRepository.save(saleOrder)),
        "Sale order approved successfully");
  }

  @Override
  public APIResponse<SaleOrderResponse> getSaleOrderById(UUID saleOrderId) {
    SaleOrder saleOrder =
        saleOrderRepository
            .findById(saleOrderId)
            .orElseThrow(() -> new GlobalException(ErrorCode.NOT_FOUND, "Sale order not found"));

    return APIResponse.success(saleOrderMapper.toSaleOrderResponse(saleOrder));
  }

  // ============================================================
  // 🔹 1. Hãng xe (EVM_STAFF, ADMIN) xem tất cả order của các đại lý
  // ============================================================
  @Override
  @PreAuthorize("hasAnyRole('EVM_STAFF', 'ADMIN')")
  public APIResponse<PageResponse<SaleOrderResponse>> getAllSaleOrdersOfDealer(
      List<OrderStatus> statuses, String keyword, Pageable pageable) {

    Page<SaleOrder> page = saleOrderRepository.searchAndFilter(statuses, keyword, pageable);
    PageResponse<SaleOrderResponse> response =
        pageMapper.toPageResponse(page, saleOrderMapper::toSaleOrderResponse);

    return APIResponse.success(response);
  }

  // ============================================================
  // 🔹 2. Đại lý (MANAGER, DEALER_STAFF) xem các order của chính đại lý mình
  // ============================================================
  @Override
  @PreAuthorize("hasAnyRole('DEALER_STAFF', 'MANAGER')")
  public APIResponse<PageResponse<SaleOrderResponse>> getAllSaleOrdersOfCurrentDealer(
      List<OrderStatus> statuses, String keyword, Pageable pageable) {

    Dealer dealer = AccountUtil.getCurrentUser().getDealer();

    Page<SaleOrder> page =
        saleOrderRepository.searchAndFilterByDealer(dealer, statuses, keyword, pageable);

    PageResponse<SaleOrderResponse> response =
        pageMapper.toPageResponse(page, saleOrderMapper::toSaleOrderResponse);

    return APIResponse.success(response);
  }

  // ============================================================
  // 🔹 3. Đại lý (MANAGER, DEALER_STAFF) xem các order đã báo giá cho khách hàng cụ thể
  // ============================================================
  @Override
  @PreAuthorize("hasAnyRole('DEALER_STAFF', 'MANAGER')")
  public APIResponse<PageResponse<SaleOrderResponse>> getAllSaleOrdersOfCurrentCustomer(
      UUID customerId, List<OrderStatus> statuses, String keyword, Pageable pageable) {

    Dealer dealer = AccountUtil.getCurrentUser().getDealer();

    Customer customer =
        customerRepository
            .findById(customerId)
            .orElseThrow(() -> new GlobalException(ErrorCode.NOT_FOUND, "Customer not found"));

    Page<SaleOrder> page =
        saleOrderRepository.searchAndFilterByDealerAndCustomer(
            dealer, customer, statuses, keyword, pageable);

    PageResponse<SaleOrderResponse> response =
        pageMapper.toPageResponse(page, saleOrderMapper::toSaleOrderResponse);

    return APIResponse.success(response);
  }

  // ============================================================
  // 🔹 4. Đại lý (MANAGER, DEALER_STAFF) xem toàn bộ order đã có báo giá của mình (mọi khách hàng)
  // ============================================================

  @PreAuthorize("hasAnyRole('DEALER_STAFF', 'MANAGER')")
  public APIResponse<PageResponse<SaleOrderResponse>> getAllQuotedSaleOrdersOfCurrentDealer(
      List<OrderStatus> statuses, String keyword, Pageable pageable) {

    Dealer dealer = AccountUtil.getCurrentUser().getDealer();

    Page<SaleOrder> page =
        saleOrderRepository.searchAndFilterQuotedOrdersByDealer(
            dealer, statuses, keyword, pageable);

    PageResponse<SaleOrderResponse> response =
        pageMapper.toPageResponse(page, saleOrderMapper::toSaleOrderResponse);

    return APIResponse.success(response);
  }

  @PreAuthorize("hasRole('DEALER_STAFF')")
  public APIResponse<PageResponse<SaleOrderResponse>> getAllSaleOrdersOfStaff(
      List<OrderStatus> statuses, String keyword, Pageable pageable) {

    Account currentStaff = AccountUtil.getCurrentUser();

    Page<SaleOrder> page =
        saleOrderRepository.searchAndFilterByAccount(currentStaff, statuses, keyword, pageable);

    PageResponse<SaleOrderResponse> response =
        pageMapper.toPageResponse(page, saleOrderMapper::toSaleOrderResponse);

    return APIResponse.success(response);
  }

  @PreAuthorize("hasRole('MANAGER')")
  public APIResponse<PageResponse<SalesByStaffResponse>> getAllSaleOrdersByemployee(
      Pageable pageable) {
    List<SalesByStaffResponse> salesByStaffResponses = new ArrayList<>();
    for (Account staff :
        accountRepository.findByRoleAndDealer(
            Role.DEALER_STAFF, AccountUtil.getCurrentUser().getDealer())) {
      List<SaleOrder> orders = saleOrderRepository.findAllSaleOrderByAccount(staff);
      // Tính tổng số đơn và tổng doanh thu
      long orderCount = orders.size();
      BigDecimal totalAmount =
          orders.stream()
              .map(SaleOrder::getTotalPrice)
              .filter(Objects::nonNull)
              .reduce(BigDecimal.ZERO, BigDecimal::add);
      // Chuyển sang response
      SalesByStaffResponse dto = new SalesByStaffResponse();
      dto.setAccountId(staff.getId());
      dto.setOrderCount(orderCount);
      dto.setAmount(totalAmount);
      salesByStaffResponses.add(dto);
    }

    // ===== Chuyển List sang Page =====
    int start = (int) pageable.getOffset();
    int end = Math.min((start + pageable.getPageSize()), salesByStaffResponses.size());
    List<SalesByStaffResponse> content = salesByStaffResponses.subList(start, end);
    Page<SalesByStaffResponse> page =
        new PageImpl<>(content, pageable, salesByStaffResponses.size());

    // ===== Chuyển sang PageResponse =====
    PageResponse<SalesByStaffResponse> response = pageMapper.toPageResponse(page, s -> s);
    return APIResponse.success(response);
  }
}
