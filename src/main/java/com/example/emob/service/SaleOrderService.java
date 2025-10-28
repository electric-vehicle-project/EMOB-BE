/* EMOB-2025 */
package com.example.emob.service;

import com.example.emob.constant.ErrorCode;
import com.example.emob.constant.OrderStatus;
import com.example.emob.constant.PaymentStatus;
import com.example.emob.constant.VehicleStatus;
import com.example.emob.entity.*;
import com.example.emob.exception.GlobalException;
import com.example.emob.mapper.PageMapper;
import com.example.emob.mapper.SaleOrderMapper;
import com.example.emob.model.request.SaleOrderItemRequest;
import com.example.emob.model.request.installment.InstallmentRequest;
import com.example.emob.model.response.APIResponse;
import com.example.emob.model.response.PageResponse;
import com.example.emob.model.response.SaleOrder.SaleOrderResponse;
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

  @Override
  @Transactional
  public APIResponse<SaleOrderResponse> createSaleOrderFromQuotation(
      Quotation quotation, List<SaleOrderItemRequest> itemRequests, PaymentStatus paymentStatus) {

    // ✅ Lấy danh sách ID item từ request
    Set<UUID> quotationItemIds =
        itemRequests.stream().map(SaleOrderItemRequest::getItemsId).collect(Collectors.toSet());

    // ✅ Tạo danh sách SaleOrderItem từ QuotationItem
    Set<SaleOrderItem> saleOrderItems =
        quotation.getQuotationItems().stream()
            .filter(item -> quotationItemIds.contains(item.getId()))
            .map(
                item -> {
                  // Tìm request tương quotationItemIds
                  SaleOrderItemRequest req =
                      itemRequests.stream()
                          .filter(r -> r.getItemsId().equals(item.getId()))
                          .findFirst()
                          .orElseThrow(
                              () ->
                                  new GlobalException(
                                      ErrorCode.INVALID_CODE, "Invalid item request"));

                  // Tính giá sau giảm
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
                  // === Tìm xe cụ thể trong kho ===
                  Set<VehicleUnit> vehicleUnits = new HashSet<>();

                  for (int i = 0; i < item.getQuantity(); i++) {
                    VehicleUnit vehicleUnit =
                        vehicleUnitRepository
                            .findFirstByInventoryAndVehicleAndColorIgnoreCaseAndStatus(
                                AccountUtil.getCurrentUser().getDealer().getInventory(),
                                item.getVehicle(),
                                item.getColor(),
                                item.getVehicleStatus())
                            .orElseThrow(
                                () ->
                                    new GlobalException(
                                        ErrorCode.NOT_FOUND,
                                        "Not found vehicle unit in inventory have model:"
                                            + item.getVehicle().getModel()
                                            + " color: "
                                            + item.getColor()
                                            + " type: "
                                            + item.getVehicleStatus()));
                    ;
                    if (vehicleUnit == null) {
                      throw new GlobalException(
                          ErrorCode.NOT_FOUND, "Vehicle unit not found in inventory");
                    }
                    vehicleUnit.setStatus(VehicleStatus.RESERVED);
                    vehicleUnits.add(vehicleUnit);
                  }

                  BigDecimal unitPrice =
                      Objects.requireNonNullElse(item.getUnitPrice(), BigDecimal.ZERO);
                  BigDecimal discountedPrice =
                      PromotionHelper.calculateDiscountedPrice(
                          unitPrice, promotion, quotation.getCustomer());
                  BigDecimal totalPrice =
                      discountedPrice.multiply(BigDecimal.valueOf(item.getQuantity()));
                  // Cập nhật cho QuotationItem
                  item.setPromotion(promotion);
                  item.setDiscountPrice(discountedPrice);
                  item.setTotalPrice(totalPrice);

                  // ✅ Tạo SaleOrderItem
                  SaleOrderItem saleItem = new SaleOrderItem();
                  saleItem.setVehicleUnits(vehicleUnits);
                  saleItem.setQuantity(item.getQuantity());
                  saleItem.setVehicleStatus(item.getVehicleStatus());
                  saleItem.setColor(item.getColor());
                  saleItem.setUnitPrice(unitPrice);
                  saleItem.setDiscountPrice(discountedPrice);
                  saleItem.setTotalPrice(totalPrice);

                  return saleItem;
                })
            .collect(Collectors.toSet());

    // ✅ Tạo SaleOrder
    SaleOrder saleOrder = new SaleOrder();
    saleOrder.setAccount(AccountUtil.getCurrentUser());
    saleOrder.setStatus(OrderStatus.CREATED);
    saleOrder.setPaymentStatus(paymentStatus);
    saleOrder.setCreatedAt(LocalDateTime.now());
    saleOrder.setSaleOrderItems(saleOrderItems);
    saleOrder.setQuotation(quotation);
    quotation.setSaleOrder(saleOrder);
    // Gán quan hệ ngược cho JPA
    saleOrderItems.forEach(item -> item.setSaleOrder(saleOrder));

    // ✅ Tính tổng tiền an toàn
    BigDecimal totalAmount =
        saleOrderItems.stream()
            .map(SaleOrderItem::getTotalPrice)
            .filter(Objects::nonNull)
            .reduce(BigDecimal.ZERO, BigDecimal::add);
    saleOrder.setVatAmount(totalAmount);
    saleOrder.setTotalPrice(totalAmount.multiply(BigDecimal.valueOf(1.1))); // VAT 10%
    // ✅ Lưu vào DB
    SaleOrder savedSaleOrder = saleOrderRepository.save(saleOrder);

    // ✅ Tạo response
    SaleOrderResponse response = saleOrderMapper.toSaleOrderResponse(savedSaleOrder);
    return APIResponse.success(response, "Sale order created successfully");
  }

  @Override
  @Transactional
  public APIResponse<SaleOrderResponse> createSaleOrderFromVehicleRequest(
      VehicleRequest vehicleRequest, PaymentStatus paymentStatus) {
    SaleOrder saleOrder = saleOrderMapper.toSaleOrderFromVehicleRequest(vehicleRequest);
    saleOrder.setPaymentStatus(paymentStatus);
    saleOrder.setVehicleRequest(vehicleRequest);
    saleOrder.setCreatedAt(LocalDateTime.now());
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
  public APIResponse<SaleOrderResponse> completeSaleOrderById(InstallmentRequest request) {
    SaleOrder saleOrder =
        saleOrderRepository
            .findById(request.getOrderId())
            .orElseThrow(() -> new GlobalException(ErrorCode.NOT_FOUND, "Sale order not found"));
    saleOrder.setStatus(OrderStatus.COMPLETED);
    if (saleOrder.getPaymentStatus().equals(PaymentStatus.INSTALLMENT)) {
      installmentPlanService.createInstallment(request);
    }
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
      List<OrderStatus> statuses, Pageable pageable) {
    Page<SaleOrder> page = saleOrderRepository.findAllWithVehicleRequest(statuses, pageable);
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
      List<OrderStatus> statuses, Pageable pageable) {
    Dealer dealer = AccountUtil.getCurrentUser().getDealer();
    Page<SaleOrder> page =
        saleOrderRepository.findAllWithVehicleRequestByDealerAndStatuses(
            dealer, statuses, pageable);
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
      UUID customerId, List<OrderStatus> statuses, Pageable pageable) {
    Dealer dealer = AccountUtil.getCurrentUser().getDealer();
    Customer customer =
        customerRepository
            .findById(customerId)
            .orElseThrow(() -> new GlobalException(ErrorCode.NOT_FOUND, "Customer not found"));

    Page<SaleOrder> page =
        saleOrderRepository.findAllWithQuotationByDealerAndCustomerAndStatuses(
            dealer, customer, statuses, pageable);

    PageResponse<SaleOrderResponse> response =
        pageMapper.toPageResponse(page, saleOrderMapper::toSaleOrderResponse);
    return APIResponse.success(response);
  }

  // ============================================================
  // 🔹 4. Đại lý (MANAGER, DEALER_STAFF) xem toàn bộ order đã có báo giá của mình (mọi khách hàng)
  // ============================================================

  @PreAuthorize("hasAnyRole('DEALER_STAFF', 'MANAGER')")
  public APIResponse<PageResponse<SaleOrderResponse>> getAllSaleOrdersByCustomer(
      List<OrderStatus> statuses, Pageable pageable) {
    Dealer dealer = AccountUtil.getCurrentUser().getDealer();
    Page<SaleOrder> page =
        saleOrderRepository.findAllWithQuotationByDealerAndStatuses(dealer, statuses, pageable);
    PageResponse<SaleOrderResponse> response =
        pageMapper.toPageResponse(page, saleOrderMapper::toSaleOrderResponse);
    return APIResponse.success(response);
  }
}
