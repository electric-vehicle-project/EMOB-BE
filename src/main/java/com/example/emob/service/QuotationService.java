/* EMOB-2025 */
package com.example.emob.service;

import com.example.emob.constant.ErrorCode;
import com.example.emob.constant.QuotationStatus;
import com.example.emob.entity.*;
import com.example.emob.exception.GlobalException;
import com.example.emob.mapper.ElectricVehicleMapper;
import com.example.emob.mapper.QuotationMapper;
import com.example.emob.model.request.quotation.QuotationItemRequest;
import com.example.emob.model.request.quotation.QuotationRequest;
import com.example.emob.model.response.APIResponse;
import com.example.emob.model.response.PageResponse;
import com.example.emob.model.response.quotation.QuotationItemResponse;
import com.example.emob.model.response.quotation.QuotationResponse;
import com.example.emob.repository.*;
import com.example.emob.service.impl.IQuotation;
import com.example.emob.util.AccountUtil;
import com.example.emob.util.PromotionHelper;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class QuotationService implements IQuotation {
  @Autowired private QuotationRepository quotationRepository;
  @Autowired private QuotationItemRepository quotationItemRepository;
  @Autowired private QuotationMapper quotationMapper;
  @Autowired private ElectricVehicleRepository electricVehicleRepository;
  @Autowired private PromotionRepository promotionRepository;
  @Autowired private CustomerRepository customerRepository;
  @Autowired private DealerRepository dealerRepository;
  @Autowired private VehiclePriceRuleService vehiclePriceRuleService;
  @Autowired private ElectricVehicleMapper electricVehicleMapper;

  @Override
  @Transactional
  public APIResponse<QuotationResponse> create(QuotationRequest request) {
    try {
      // Lấy entities cần thiết
      Customer customer =
          customerRepository
              .findById(request.getCustomerId())
              .orElseThrow(() -> new GlobalException(ErrorCode.NOT_FOUND, "Customer not found"));
      Dealer dealer =
          dealerRepository
              .findById(request.getDealerId())
              .orElseThrow(() -> new GlobalException(ErrorCode.NOT_FOUND, "Dealer not found"));

      // Khởi tạo quotation
      Quotation quotation = new Quotation();
      quotation.setCustomer(customer);
      quotation.setDealer(dealer);
      quotation.setValidUntil(request.getValidUntil());
      quotation.setStatus(QuotationStatus.PENDING);
      quotation.setAccount(AccountUtil.getCurrentUser());
      quotation.setCreatedAt(LocalDateTime.now());

      Set<QuotationItem> quotationItems = new HashSet<>();
      Set<QuotationItemResponse> itemResponses = new HashSet<>();

      BigDecimal totalPrice = BigDecimal.ZERO;
      int totalQuantity = 0;

      for (QuotationItemRequest itemRequest : request.getItems()) {
        QuotationItem item = createQuotationItem(itemRequest);
        VehiclePriceRule priceRule = vehiclePriceRuleService.getRule(item.getVehicleStatus());

        // Hệ số giá
        BigDecimal multiplier = BigDecimal.valueOf(priceRule.getMultiplier());
        BigDecimal retailPrice = item.getVehicle().getRetailPrice();
        BigDecimal price = retailPrice.multiply(multiplier);

        // Tính giá sau khuyến mãi
        BigDecimal discountedPrice;
        if (itemRequest.getPromotionId() != null) {
          Promotion promotion =
              promotionRepository
                  .findById(itemRequest.getPromotionId())
                  .orElseThrow(
                      () -> new GlobalException(ErrorCode.NOT_FOUND, "Promotion not found"));
          PromotionHelper.checkPromotionValid(promotion);
          discountedPrice = PromotionHelper.calculateDiscountedPrice(price, promotion, customer);
          item.setPromotion(promotion);
        } else {
          discountedPrice = PromotionHelper.calculateDiscountedPrice(price, customer);
        }

        // Gán các giá trị vào item
        item.setUnitPrice(price);
        item.setDiscountPrice(discountedPrice);
        item.setTotalPrice(discountedPrice.multiply(BigDecimal.valueOf(item.getQuantity())));
        item.setQuotation(quotation); // liên kết ngược về quotation

        quotationItems.add(item);

        totalPrice = totalPrice.add(item.getTotalPrice());
        totalQuantity += item.getQuantity();

        // Map response item
        itemResponses.add(
            QuotationItemResponse.builder()
                .id(item.getId())
                .vehicle(electricVehicleMapper.toVehicleResponse(item.getVehicle()))
                .promotionId(item.getPromotion() != null ? item.getPromotion().getId() : null)
                .vehicleStatus(item.getVehicleStatus())
                .color(item.getColor())
                .quantity(item.getQuantity())
                .unitPrice(item.getUnitPrice())
                .discountPrice(item.getDiscountPrice())
                .totalPrice(item.getTotalPrice())
                .build());
      }

      quotation.setQuotationItems(quotationItems);
      quotation.setTotalPrice(totalPrice);
      quotation.setTotalQuantity(totalQuantity);

      // Lưu cả quotation (cascade ALL → items tự lưu)
      Quotation savedQuotation = quotationRepository.save(quotation);

      // Map sang response
      QuotationResponse quotationResponse = quotationMapper.toQuotationResponse(savedQuotation);
      quotationResponse.setItems(itemResponses);

      return APIResponse.success(quotationResponse, "Create quotation successfully");
    } catch (Exception e) {
      throw new GlobalException(ErrorCode.INVALID_CODE, e.getMessage());
    }
  }

  @Override
  @Transactional
  public APIResponse<QuotationResponse> update(UUID id, QuotationRequest request) {
    //    try {
    //      Quotation quotation = quotationRepository.findById(id)
    //              .orElseThrow(() -> new GlobalException(ErrorCode.NOT_FOUND, "Quotation not
    // found"));
    //
    //      // Optional update: customer
    //      if (request.getCustomerId() != null) {
    //        Customer customer = customerRepository.findById(request.getCustomerId())
    //                .orElseThrow(() -> new GlobalException(ErrorCode.NOT_FOUND, "Customer not
    // found"));
    //        quotation.setCustomer(customer);
    //      }
    //
    //      // Optional update: dealer
    //      if (request.getDealerId() != null) {
    //        Dealer dealer = dealerRepository.findById(request.getDealerId())
    //                .orElseThrow(() -> new GlobalException(ErrorCode.NOT_FOUND, "Dealer not
    // found"));
    //        quotation.setDealer(dealer);
    //      }
    //
    //      // Optional update: validUntil
    //      if (request.getValidUntil() > 0) {
    //        quotation.setValidUntil(request.getValidUntil());
    //      }
    //
    //
    //      // Optional update: quotation items
    //      if (request.getItems() != null && !request.getItems().isEmpty()) {
    //        Map<UUID, QuotationItem> existingItems = quotation.getQuotationItems()
    //                .stream().collect(Collectors.toMap(QuotationItem::getId,
    // Function.identity()));
    //
    //        Set<QuotationItem> updatedItems = new HashSet<>();
    //        BigDecimal totalPrice = BigDecimal.ZERO;
    //        int totalQuantity = 0;
    //        Set<QuotationItemResponse> itemResponses = new HashSet<>();
    //
    //        for (QuotationItemRequest itemRequest : request.getItems()) {
    //          QuotationItem item;
    //          if (itemRequest.getId() != null && existingItems.containsKey(itemRequest.getId())) {
    //            // Update existing item
    //            item = existingItems.get(itemRequest.getId());
    //          } else {
    //            // Create new item
    //            item = createQuotationItem(itemRequest);
    //            item.setQuotation(quotation);
    //          }
    //
    //          // Tính giá và discount
    //          VehiclePriceRule priceRule =
    // vehiclePriceRuleService.getRule(item.getVehicleStatus());
    //          BigDecimal multiplier = BigDecimal.valueOf(priceRule.getMultiplier());
    //          BigDecimal price = item.getVehicle().getRetailPrice().multiply(multiplier);
    //
    //          BigDecimal discountedPrice;
    //          if (itemRequest.getPromotionId() != null) {
    //            Promotion promotion = promotionRepository.findById(itemRequest.getPromotionId())
    //                    .orElseThrow(() -> new GlobalException(ErrorCode.NOT_FOUND, "Promotion not
    // found"));
    //            PromotionHelper.checkPromotionValid(promotion);
    //            discountedPrice = PromotionHelper.calculateDiscountedPrice(price, promotion,
    // quotation.getCustomer());
    //            item.setPromotion(promotion);
    //          } else {
    //            discountedPrice = PromotionHelper.calculateDiscountedPrice(price,
    // quotation.getCustomer());
    //          }
    //
    //          item.setUnitPrice(price);
    //          item.setDiscountPrice(discountedPrice);
    //
    // item.setTotalPrice(discountedPrice.multiply(BigDecimal.valueOf(item.getQuantity())));
    //
    //          updatedItems.add(item);
    //
    //          totalPrice = totalPrice.add(item.getTotalPrice());
    //          totalQuantity += item.getQuantity();
    //
    //          itemResponses.add(
    //                  QuotationItemResponse.builder()
    //                          .id(item.getId())
    //                          .vehicle(electricVehicleMapper.toVehicleResponse(item.getVehicle()))
    //                          .promotionId(item.getPromotion() != null ?
    // item.getPromotion().getId() : null)
    //                          .vehicleStatus(item.getVehicleStatus())
    //                          .color(item.getColor())
    //                          .quantity(item.getQuantity())
    //                          .unitPrice(item.getUnitPrice())
    //                          .discountPrice(item.getDiscountPrice())
    //                          .totalPrice(item.getTotalPrice())
    //                          .build()
    //          );
    //        }
    //
    //        quotation.setQuotationItems(updatedItems);
    //        quotation.setTotalPrice(totalPrice);
    //        quotation.setTotalQuantity(totalQuantity);
    //        quotationResponse.setItems(itemResponses);
    //      }
    //
    //      Quotation savedQuotation = quotationRepository.save(quotation);
    //      QuotationResponse quotationResponse =
    // quotationMapper.toQuotationResponse(savedQuotation);
    //
    //      return APIResponse.success(quotationResponse, "Update quotation successfully");
    //
    //    } catch (Exception e) {
    //      throw new GlobalException(ErrorCode.INVALID_CODE, e.getMessage());
    //    }
    return null;
  }

  @Override
  public APIResponse<QuotationResponse> delete(UUID id) {
    return null;
  }

  @Override
  public APIResponse<QuotationResponse> get(UUID id) {
    return null;
  }

  @Override
  public APIResponse<PageResponse<QuotationResponse>> getAll(Pageable pageable) {
    return null;
  }

  private QuotationItem createQuotationItem(QuotationItemRequest request) {
    ElectricVehicle vehicle =
        electricVehicleRepository
            .findById(request.getVehicleId())
            .orElseThrow(() -> new GlobalException(ErrorCode.NOT_FOUND, "Vehicle not found"));

    return QuotationItem.builder()
        .color(request.getColor())
        .quantity(request.getQuantity())
        .vehicleStatus(request.getVehicleStatus())
        .vehicle(vehicle)
        .build();
  }
}
