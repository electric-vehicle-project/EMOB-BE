/* EMOB-2025 */
package com.example.emob.service;

import com.example.emob.constant.ErrorCode;
import com.example.emob.constant.QuotationStatus;
import com.example.emob.entity.*;
import com.example.emob.exception.GlobalException;
import com.example.emob.mapper.PageMapper;
import com.example.emob.mapper.QuotationMapper;
import com.example.emob.model.request.SaleOrderItemRequest;
import com.example.emob.model.request.quotation.QuotationItemRequest;
import com.example.emob.model.request.quotation.QuotationItemUpdateRequest;
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
import java.util.function.Function;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
public class QuotationService implements IQuotation {
  @Autowired private QuotationRepository quotationRepository;
  @Autowired private QuotationMapper quotationMapper;
  @Autowired private ElectricVehicleRepository electricVehicleRepository;
  @Autowired private PromotionRepository promotionRepository;
  @Autowired private CustomerRepository customerRepository;
  @Autowired private VehiclePriceRuleService vehiclePriceRuleService;
  @Autowired private QuotationItemRepository quotationItemRepository;
  @Autowired private PageMapper pageMapper;
  @Autowired private SaleOrderService saleOrderService;

  @Scheduled(cron = "0 0 0 * * *") // Giờ VN, check mỗi ngày một lần
  public void checkExpiredQuotations() {
    List<Quotation> quotations =
        quotationRepository.findAllByIsDeletedFalseAndStatus(QuotationStatus.PENDING);
    LocalDateTime now = LocalDateTime.now();

    for (Quotation quotation : quotations) {
      LocalDateTime baseDate =
          quotation.getUpdatedAt() != null ? quotation.getUpdatedAt() : quotation.getCreatedAt();

      LocalDateTime expiryDate = baseDate.plusDays(quotation.getValidUntil());

      if (now.isAfter(expiryDate)) {
        quotation.setStatus(QuotationStatus.EXPIRED);
        quotation.setUpdatedAt(now);
        log.info("Quotation [{}] expired at {}", quotation.getId(), expiryDate);
      }
    }
    quotationRepository.saveAll(quotations);
  }

  @Override
  @Transactional
  public APIResponse<QuotationResponse> create(QuotationRequest<QuotationItemRequest> request) {
    try {
      // Lấy Customer và Dealer liên quan
      Customer customer =
          customerRepository
              .findById(request.getCustomerId())
              .orElseThrow(() -> new GlobalException(ErrorCode.NOT_FOUND, "Customer not found"));

      // Khởi tạo quotation
      Quotation quotation =
          Quotation.builder()
              .dealer(customer.getDealer())
              .customer(customer)
              .validUntil(request.getValidUntil())
              .status(QuotationStatus.PENDING)
              .account(AccountUtil.getCurrentUser())
              .createdAt(LocalDateTime.now())
              .build();

      Set<QuotationItem> quotationItems = new HashSet<>();
      Set<QuotationItemResponse> itemResponses = new HashSet<>();
      BigDecimal totalPrice = BigDecimal.ZERO;
      int totalQuantity = 0;

      for (QuotationItemRequest itemRequest : request.getItems()) {
        QuotationItem item = createQuotationItem(itemRequest);
        VehiclePriceRule priceRule = vehiclePriceRuleService.getRule(item.getVehicleStatus());

        BigDecimal multiplier = BigDecimal.valueOf(priceRule.getMultiplier());
        BigDecimal retailPrice = item.getVehicle().getRetailPrice();
        BigDecimal basePrice = retailPrice.multiply(multiplier);

        // Tính giá sau khuyến mãi
        BigDecimal discountedPrice;
        if (itemRequest.getPromotionId() != null) {
          Promotion promotion =
              promotionRepository
                  .findById(itemRequest.getPromotionId())
                  .orElseThrow(
                      () -> new GlobalException(ErrorCode.NOT_FOUND, "Promotion not found"));
          // check promotion tồn tại trong vehicle
          PromotionHelper.checkPromotionExists(promotion, item.getVehicle());
          PromotionHelper.checkPromotionValid(promotion);
          discountedPrice =
              PromotionHelper.calculateDiscountedPrice(basePrice, promotion, customer);
          item.setPromotion(promotion);
        } else {
          discountedPrice = PromotionHelper.calculateDiscountedPrice(basePrice, null, customer);
        }

        // Gán các giá trị vào item
        item.setUnitPrice(basePrice);
        item.setDiscountPrice(discountedPrice);
        item.setTotalPrice(discountedPrice.multiply(BigDecimal.valueOf(item.getQuantity())));
        item.setQuotation(quotation);

        quotationItems.add(item);
        totalPrice = totalPrice.add(item.getTotalPrice());
        totalQuantity += item.getQuantity();
      }
      BigDecimal vatRate = new BigDecimal("0.1"); // 10%
      BigDecimal vatAmount = totalPrice.multiply(vatRate);
      BigDecimal totalWithVat = totalPrice.add(vatAmount);

      quotation.setQuotationItems(quotationItems);
      quotation.setVatAmount(vatAmount);
      quotation.setTotalPrice(totalWithVat);
      quotation.setTotalQuantity(totalQuantity);

      Quotation savedQuotation = quotationRepository.save(quotation);

      for (QuotationItem saveItem : savedQuotation.getQuotationItems()) {
        // Map sang response
        itemResponses.add(
            QuotationItemResponse.builder()
                .id(saveItem.getId())
                .vehicleId(saveItem.getVehicle().getId())
                .promotionId(
                    saveItem.getPromotion() != null ? saveItem.getPromotion().getId() : null)
                .vehicleStatus(saveItem.getVehicleStatus())
                .color(saveItem.getColor())
                .quantity(saveItem.getQuantity())
                .unitPrice(saveItem.getUnitPrice())
                .discountPrice(saveItem.getDiscountPrice())
                .totalPrice(saveItem.getTotalPrice())
                .build());
      }

      QuotationResponse quotationResponse = quotationMapper.toQuotationResponse(savedQuotation);
      quotationResponse.setItems(itemResponses);

      return APIResponse.success(quotationResponse, "Create quotation successfully");
    } catch (GlobalException e) {
      throw e; // giữ nguyên lỗi nghiệp vụ
    } catch (Exception e) {
      throw new GlobalException(ErrorCode.INVALID_CODE, e.getMessage());
    }
  }

  @Override
  @Transactional
  public APIResponse<QuotationResponse> update(
      UUID id, QuotationRequest<QuotationItemUpdateRequest> request) {
    try {
      Quotation quotation =
          quotationRepository
              .findById(id)
              .orElseThrow(() -> new GlobalException(ErrorCode.NOT_FOUND, "Quotation not found"));

      // --- Cập nhật customer ---
      if (request.getCustomerId() != null) {
        Customer customer =
            customerRepository
                .findById(request.getCustomerId())
                .orElseThrow(() -> new GlobalException(ErrorCode.NOT_FOUND, "Customer not found"));
        quotation.setCustomer(customer);
      }

      // --- Cập nhật thời hạn ---
      if (request.getValidUntil() != null && request.getValidUntil() > 0) {
        quotation.setValidUntil(request.getValidUntil());
      }

      Map<UUID, QuotationItem> existingItems =
          quotation.getQuotationItems().stream()
              .collect(Collectors.toMap(QuotationItem::getId, Function.identity()));

      Set<QuotationItem> updatedItems = new HashSet<>();
      Set<QuotationItemResponse> itemResponses = new HashSet<>();

      BigDecimal totalPrice = BigDecimal.ZERO;
      int totalQuantity = 0;

      // --- Lặp qua các item request ---
      for (QuotationItemUpdateRequest itemReq :
          Optional.ofNullable(request.getItems()).orElse(Collections.emptyList())) {
        QuotationItem item;

        // Nếu có ID → update
        if (itemReq.getId() != null && existingItems.containsKey(itemReq.getId())) {
          item = existingItems.get(itemReq.getId());
        } else {
          // Nếu không có ID → tạo mới
          item = new QuotationItem();
          item.setQuotation(quotation);
        }

        // --- Cập nhật dữ liệu ---
        ElectricVehicle vehicle =
            electricVehicleRepository
                .findById(itemReq.getVehicleId())
                .orElseThrow(() -> new GlobalException(ErrorCode.NOT_FOUND, "Vehicle not found"));
        item.setVehicle(vehicle);
        item.setVehicleStatus(itemReq.getVehicleStatus());
        item.setColor(itemReq.getColor());
        item.setQuantity(itemReq.getQuantity());
        item.setDeleted(false); // Item được cập nhật hoặc thêm mới là active

        // --- Tính giá ---
        VehiclePriceRule priceRule = vehiclePriceRuleService.getRule(itemReq.getVehicleStatus());
        BigDecimal basePrice =
            vehicle.getRetailPrice().multiply(BigDecimal.valueOf(priceRule.getMultiplier()));

        BigDecimal discountedPrice;
        if (itemReq.getPromotionId() != null) {
          Promotion promotion =
              promotionRepository
                  .findById(itemReq.getPromotionId())
                  .orElseThrow(
                      () -> new GlobalException(ErrorCode.NOT_FOUND, "Promotion not found"));
          PromotionHelper.checkPromotionExists(promotion, item.getVehicle());
          PromotionHelper.checkPromotionValid(promotion);
          discountedPrice =
              PromotionHelper.calculateDiscountedPrice(
                  basePrice, promotion, quotation.getCustomer());
          item.setPromotion(promotion);
        } else {
          discountedPrice =
              PromotionHelper.calculateDiscountedPrice(basePrice, null, quotation.getCustomer());
          item.setPromotion(null);
        }

        item.setUnitPrice(basePrice);
        item.setDiscountPrice(discountedPrice);

        item.setTotalPrice(discountedPrice.multiply(BigDecimal.valueOf(item.getQuantity())));

        updatedItems.add(item);
      }

      // --- Xử lý item bị xóa ---
      Set<UUID> updatedIds =
          request.getItems().stream()
              .map(QuotationItemUpdateRequest::getId)
              .filter(Objects::nonNull)
              .collect(Collectors.toSet());

      Set<QuotationItem> itemsToRemove =
          quotation.getQuotationItems().stream()
              .filter(item -> !updatedIds.contains(item.getId()))
              .collect(Collectors.toSet());
      // Đánh dấu isDeleted = true
      for (QuotationItem itemToRemove : itemsToRemove) {
        itemToRemove.setDeleted(true);
      }
      // --- Gộp danh sách lại ---
      quotation.getQuotationItems().clear();
      quotation.getQuotationItems().addAll(updatedItems);
      quotation.getQuotationItems().addAll(itemsToRemove);

      // --- Tính lại tổng ---
      for (QuotationItem item : updatedItems) {
        totalPrice = totalPrice.add(item.getTotalPrice());
        totalQuantity += item.getQuantity();
      }
      quotation.setVatAmount(totalPrice);
      quotation.setTotalPrice(totalPrice.multiply(BigDecimal.valueOf(1.1)));
      quotation.setTotalQuantity(totalQuantity);
      quotation.setUpdatedAt(LocalDateTime.now());

      Quotation savedQuotation = quotationRepository.save(quotation);

      QuotationResponse response = quotationMapper.toQuotationResponse(savedQuotation);

      return APIResponse.success(response, "Update quotation successfully");

    } catch (GlobalException e) {
      throw e;
    } catch (Exception e) {
      throw new GlobalException(
          ErrorCode.INVALID_CODE, "Error updating quotation: " + e.getMessage());
    }
  }

  @Override
  public APIResponse<QuotationResponse> delete(UUID id) {
    Quotation quotation =
        quotationRepository
            .findById(id)
            .orElseThrow(() -> new GlobalException(ErrorCode.NOT_FOUND, "Quotation not found"));
    quotation.setDeleted(true);
    for (QuotationItem item : quotation.getQuotationItems()) {
      item.setDeleted(true);
    }
    quotationRepository.save(quotation);
    return APIResponse.success(
        quotationMapper.toQuotationResponse(quotation), "Delete quotation successfully");
  }


  public APIResponse<QuotationResponse> reject(UUID id){
    Quotation quotation =
            quotationRepository
                    .findById(id)
                    .orElseThrow(() -> new GlobalException(ErrorCode.NOT_FOUND, "Quotation not found"));
    quotation.setStatus(QuotationStatus.REJECTED);
    Quotation savedQuotation = quotationRepository.save(quotation);
    return APIResponse.success(
            quotationMapper.toQuotationResponse(savedQuotation), "Reject quotation successfully");
  }

  @Override
  @PreAuthorize("hasAnyRole('MANAGER','DEALER_STAFF')")
  public APIResponse<QuotationResponse> get(UUID id) {
    Quotation quotation =
        quotationRepository
            .findById(id)
            .orElseThrow(() -> new GlobalException(ErrorCode.NOT_FOUND, "Quotation not found"));
    Set<QuotationItemResponse> itemResponses = new HashSet<>();
    for (QuotationItem savedItem : quotation.getQuotationItems()) {
      if (!savedItem.isDeleted()) {
        itemResponses.add(
            QuotationItemResponse.builder()
                .id(savedItem.getId())
                .vehicleId(savedItem.getVehicle().getId())
                .promotionId(
                    savedItem.getPromotion() != null ? savedItem.getPromotion().getId() : null)
                .vehicleStatus(savedItem.getVehicleStatus())
                .color(savedItem.getColor())
                .quantity(savedItem.getQuantity())
                .unitPrice(savedItem.getUnitPrice())
                .discountPrice(savedItem.getDiscountPrice())
                .totalPrice(savedItem.getTotalPrice())
                .build());
      }
    }
    QuotationResponse response = quotationMapper.toQuotationResponse(quotation);
    response.setItems(itemResponses);
    return APIResponse.success(response, "Get quotation successfully");
  }

  @Override
  //  @PreAuthorize("hasAnyRole('MANAGER','DEALER_STAFF')")
  public APIResponse<PageResponse<QuotationResponse>> getAll(
      Pageable pageable, String keyword, List<QuotationStatus> status) {
    Dealer dealer = AccountUtil.getCurrentUser().getDealer();
    if (dealer == null) {
      throw new GlobalException(ErrorCode.UNAUTHORIZED, "Only dealers can view quotations.");
    }
    Page<Quotation> page = quotationRepository.searchAndFilter(dealer, keyword, status, pageable);

    PageResponse<QuotationResponse> pageResponse =
        pageMapper.toPageResponse(page, quotationMapper::toQuotationResponse);

    return APIResponse.success(pageResponse, "Get all quotations successfully");
  }

  @PreAuthorize("hasRole('DEALER_STAFF')")
  public APIResponse<PageResponse<QuotationResponse>> getAllOfDealerStaff(Pageable pageable) {

    Page<Quotation> page =
        quotationRepository.findAllByIsDeletedFalseAndDealerAndAccount(
            AccountUtil.getCurrentUser().getDealer(), AccountUtil.getCurrentUser(), pageable);
    // Gói kết quả vào PageResponse
    PageResponse<QuotationResponse> pageResponse =
        pageMapper.toPageResponse(page, quotationMapper::toQuotationResponse);

    return APIResponse.success(pageResponse, "Get all quotations successfully");
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

  @Transactional
  public APIResponse<QuotationResponse> approveQuotation(
      UUID id, List<SaleOrderItemRequest> itemRequests) {
    Quotation quotation =
        quotationRepository
            .findById(id)
            .orElseThrow(() -> new GlobalException(ErrorCode.NOT_FOUND, "Quotation not found"));
    if (AccountUtil.getCurrentUser() != quotation.getAccount()) {
      throw new GlobalException(
          ErrorCode.UNAUTHENTICATED, "You are not allowed to approve this quotation");
    }
    quotation.setStatus(QuotationStatus.APPROVED);
    Quotation savedQuotation = quotationRepository.save(quotation);
    saleOrderService.createSaleOrderFromQuotation(quotation, itemRequests);
    return APIResponse.success(
        quotationMapper.toQuotationResponse(savedQuotation), "Approve quotation successfully");
  }
}
