/* EMOB-2025 */
package com.example.emob.service;

import com.example.emob.constant.ErrorCode;
import com.example.emob.constant.OrderStatus;
import com.example.emob.constant.PaymentStatus;
import com.example.emob.entity.*;
import com.example.emob.exception.GlobalException;
import com.example.emob.mapper.PageMapper;
import com.example.emob.mapper.SaleOrderMapper;
import com.example.emob.model.request.SaleOrderItemRequest;
import com.example.emob.model.request.installment.InstallmentRequest;
import com.example.emob.model.response.APIResponse;
import com.example.emob.model.response.PageResponse;
import com.example.emob.model.response.SaleOrder.SaleOrderResponse;
import com.example.emob.repository.PromotionRepository;
import com.example.emob.repository.SaleOrderRepository;
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

                  BigDecimal unitPrice =
                      Objects.requireNonNullElse(item.getUnitPrice(), BigDecimal.ZERO);
                  BigDecimal discountedPrice =
                      PromotionHelper.calculateDiscountedPrice(
                          unitPrice, promotion, quotation.getCustomer());
                  BigDecimal totalPrice =
                      discountedPrice.multiply(BigDecimal.valueOf(item.getQuantity()));

                  // Cập nhật cho QuotationItem
                  item.setPromotion(promotion);
                  item.setTotalPrice(totalPrice);

                  // ✅ Tạo SaleOrderItem
                  SaleOrderItem saleItem = new SaleOrderItem();
                  saleItem.setVehicle(item.getVehicle());
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
    saleOrder.setCustomer(quotation.getCustomer());
    saleOrder.setDealer(quotation.getDealer());
    saleOrder.setAccount(AccountUtil.getCurrentUser());
    saleOrder.setStatus(OrderStatus.CREATED);
    saleOrder.setPaymentStatus(paymentStatus);
    saleOrder.setCreatedAt(LocalDateTime.now());
    saleOrder.setSaleOrderItems(saleOrderItems);
    saleOrder.setQuotation(quotation);

    // Gán quan hệ ngược cho JPA
    saleOrderItems.forEach(item -> item.setSaleOrder(saleOrder));

    // ✅ Tính tổng tiền an toàn
    BigDecimal totalAmount =
        saleOrderItems.stream()
            .map(SaleOrderItem::getTotalPrice)
            .filter(Objects::nonNull)
            .reduce(BigDecimal.ZERO, BigDecimal::add);

    saleOrder.setTotalPrice(totalAmount);

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
    SaleOrder saleOrder = saleOrderMapper.toSaleOrder(vehicleRequest);
    saleOrder.setPaymentStatus(paymentStatus);
    saleOrder.setVehicleRequest(vehicleRequest);
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

  @Override
  public APIResponse<PageResponse<SaleOrderResponse>> getAllSaleOrdersOfDealer(Pageable pageable) {
    Dealer dealer = AccountUtil.getCurrentUser().getDealer();
    Page<SaleOrder> page =
        saleOrderRepository.findAllByDealerAndVehicleRequestIsNotNull(dealer, pageable);
    PageResponse<SaleOrderResponse> response =
        pageMapper.toPageResponse(page, saleOrderMapper::toSaleOrderResponse);
    return APIResponse.success(response);
  }

  public APIResponse<PageResponse<SaleOrderResponse>> getAllSaleOrdersByCustomer(
      Pageable pageable) {
    Dealer dealer = AccountUtil.getCurrentUser().getDealer();
    Page<SaleOrder> page =
        saleOrderRepository.findAllByDealerAndQuotationIsNotNull(dealer, pageable);
    PageResponse<SaleOrderResponse> response =
        pageMapper.toPageResponse(page, saleOrderMapper::toSaleOrderResponse);
    return APIResponse.success(response);
  }
}
