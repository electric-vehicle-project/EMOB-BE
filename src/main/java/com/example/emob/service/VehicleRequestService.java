/* EMOB-2025 */
package com.example.emob.service;

import com.example.emob.constant.*;
import com.example.emob.entity.*;
import com.example.emob.entity.VehicleRequest;
import com.example.emob.exception.GlobalException;
import com.example.emob.mapper.PageMapper;
import com.example.emob.mapper.VehicleRequestMapper;
import com.example.emob.model.request.vehicleRequest.VehicleRequestItemRequest;
import com.example.emob.model.request.vehicleRequest.VehicleRequestItemUpdateRequest;
import com.example.emob.model.request.vehicleRequest.VehicleRequestRequest;
import com.example.emob.model.response.APIResponse;
import com.example.emob.model.response.PageResponse;
import com.example.emob.model.response.vehicleRequest.VehicleRequestItemResponse;
import com.example.emob.model.response.vehicleRequest.VehicleRequestResponse;
import com.example.emob.repository.DealerDiscountPolicyRepository;
import com.example.emob.repository.ElectricVehicleRepository;
import com.example.emob.repository.VehicleRequestRepository;
import com.example.emob.service.impl.IVehicleRequest;
import com.example.emob.util.AccountUtil;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class VehicleRequestService implements IVehicleRequest {
  @Autowired private VehicleRequestRepository vehiclerequestRepository;
  @Autowired private ElectricVehicleRepository electricVehicleRepository;
  @Autowired private VehiclePriceRuleService vehiclePriceRuleService;
  @Autowired private DealerDiscountPolicyRepository dealerDiscountPolicyRepository;
  @Autowired private VehicleRequestMapper vehicleRequestMapper;
  @Autowired private PageMapper pageMapper;
  @Autowired private SaleOrderService saleOrderService;

  @Override
  public APIResponse<VehicleRequestResponse> create(
      VehicleRequestRequest<VehicleRequestItemRequest> request) {
    try {
      VehicleRequest vehicleRequest =
          VehicleRequest.builder()
              .createdAt(LocalDateTime.now())
              .dealer(AccountUtil.getCurrentUser().getDealer())
              .status(VehicleRequestStatus.PENDING)
              .build();

      Set<VehicleRequestItem> vehicleRequestItems = new HashSet<>();
      BigDecimal totalPrice = BigDecimal.ZERO;
      int totalQuantity = 0;

      for (VehicleRequestItemRequest itemRequest : request.getItems()) {
        VehicleRequestItem item = createVehicleRequestItem(itemRequest);
        VehiclePriceRule priceRule = vehiclePriceRuleService.getRule(item.getVehicleStatus());
        DealerDiscountPolicy dealerDiscountPolicy =
            dealerDiscountPolicyRepository.findByDealerAndVehicleAndStatus(
                AccountUtil.getCurrentUser().getDealer(),
                item.getVehicle(),
                DiscountPolicyStatus.ACTIVE);

        BigDecimal basePrice;
        BigDecimal multiplier = BigDecimal.valueOf(priceRule.getMultiplier());
        BigDecimal customMultiplier;
        if (dealerDiscountPolicy != null) {
          customMultiplier = BigDecimal.valueOf(dealerDiscountPolicy.getCustomMultiplier());
        } else {
          customMultiplier = BigDecimal.ONE;
        }

        BigDecimal importPrice = item.getVehicle().getImportPrice();
        if (dealerDiscountPolicy.getFinalPrice() != null) {
          // có giá cố định
          basePrice = dealerDiscountPolicy.getFinalPrice();
        } else {
          // áp dụng chiết khấu
          basePrice = importPrice.multiply(multiplier).multiply(customMultiplier);
        }

        // Gán các giá trị vào item
        item.setUnitPrice(basePrice);
        item.setTotalPrice(basePrice.multiply(BigDecimal.valueOf(item.getQuantity())));
        item.setVehicleRequest(vehicleRequest);

        vehicleRequestItems.add(item);
        totalPrice = totalPrice.add(item.getTotalPrice());
        totalQuantity += item.getQuantity();
      }

      vehicleRequest.setTotalQuantity(totalQuantity);
      vehicleRequest.setTotalPrice(totalPrice);
      vehicleRequest.setVehicleRequestItems(vehicleRequestItems);

      VehicleRequest savedVehicleRequest = vehiclerequestRepository.save(vehicleRequest);

      VehicleRequestResponse response =
          vehicleRequestMapper.toVehicleRequestResponse(savedVehicleRequest);

      return APIResponse.success(response, "Create quotation successfully");
    } catch (GlobalException e) {
      throw e; // giữ nguyên lỗi nghiệp vụ
    } catch (Exception e) {
      throw new GlobalException(ErrorCode.INVALID_CODE, e.getMessage());
    }
  }

  @Override
  @Transactional
  public APIResponse<VehicleRequestResponse> update(
      UUID id, VehicleRequestRequest<VehicleRequestItemUpdateRequest> request) {
    try {
      VehicleRequest vehicleRequest =
          vehiclerequestRepository
              .findById(id)
              .orElseThrow(
                  () -> new GlobalException(ErrorCode.NOT_FOUND, "Vehicle request not found"));

      if (vehicleRequest.getStatus() != VehicleRequestStatus.PENDING) {
        throw new GlobalException(
            ErrorCode.INVALID_CODE, "Only pending vehicle requests can be updated");
      }

      Map<UUID, VehicleRequestItem> existingItems =
          vehicleRequest.getVehicleRequestItems().stream()
              .collect(Collectors.toMap(VehicleRequestItem::getId, Function.identity()));

      Set<VehicleRequestItem> updatedItems = new HashSet<>();
      Set<VehicleRequestItemResponse> itemResponses = new HashSet<>();

      BigDecimal totalPrice = BigDecimal.ZERO;
      int totalQuantity = 0;

      for (VehicleRequestItemUpdateRequest itemReq :
          Optional.ofNullable(request.getItems()).orElse(Collections.emptyList())) {
        VehicleRequestItem item;

        // Nếu có id, lấy item cũ ra để update
        if (itemReq.getId() != null && existingItems.containsKey(itemReq.getId())) {
          item = existingItems.get(itemReq.getId());
        } else {
          // Nếu không có id → tạo mới
          item = new VehicleRequestItem();
          item.setVehicleRequest(vehicleRequest);
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

        // --- Tính giá ---
        VehiclePriceRule priceRule = vehiclePriceRuleService.getRule(item.getVehicleStatus());
        DealerDiscountPolicy dealerDiscountPolicy =
            dealerDiscountPolicyRepository.findByDealerAndVehicleAndStatus(
                AccountUtil.getCurrentUser().getDealer(),
                item.getVehicle(),
                DiscountPolicyStatus.ACTIVE);
        BigDecimal basePrice;
        BigDecimal multiplier = BigDecimal.valueOf(priceRule.getMultiplier());
        BigDecimal customMultiplier;
        if (dealerDiscountPolicy != null) {
          customMultiplier = BigDecimal.valueOf(dealerDiscountPolicy.getCustomMultiplier());
        } else {
          customMultiplier = BigDecimal.ONE;
        }

        BigDecimal importPrice = item.getVehicle().getImportPrice();
        if (dealerDiscountPolicy.getFinalPrice() != null) {
          // có giá cố định
          basePrice = dealerDiscountPolicy.getFinalPrice();
        } else {
          // áp dụng chiết khấu
          basePrice = importPrice.multiply(multiplier).multiply(customMultiplier);
        }

        // Gán các giá trị vào item
        item.setUnitPrice(basePrice);
        item.setTotalPrice(basePrice.multiply(BigDecimal.valueOf(item.getQuantity())));
        item.setVehicleRequest(vehicleRequest);

        updatedItems.add(item);
        totalPrice = totalPrice.add(item.getTotalPrice());
        totalQuantity += item.getQuantity();
      }

      // Xóa những item không còn trong request
      Set<UUID> updatedIds =
          request.getItems().stream()
              .map(VehicleRequestItemUpdateRequest::getId)
              .filter(Objects::nonNull)
              .collect(Collectors.toSet());

      // Tìm những item không còn trong request (cần đánh dấu xóa)
      Set<VehicleRequestItem> itemsToRemove =
          vehicleRequest.getVehicleRequestItems().stream()
              .filter(item -> !updatedIds.contains(item.getId()))
              .collect(Collectors.toSet());

      // Đánh dấu là đã xóa
      for (VehicleRequestItem itemToRemove : itemsToRemove) {
        itemToRemove.setDeleted(true);
      }
      // Hợp nhất danh sách item
      vehicleRequest.getVehicleRequestItems().clear();
      vehicleRequest.getVehicleRequestItems().addAll(updatedItems);
      vehicleRequest.getVehicleRequestItems().addAll(itemsToRemove);

      for (VehicleRequestItem item : updatedItems) {
        totalPrice = totalPrice.add(item.getTotalPrice());
        totalQuantity += item.getQuantity();
      }

      vehicleRequest.setTotalPrice(totalPrice);
      vehicleRequest.setTotalQuantity(totalQuantity);
      VehicleRequest savedRequest = vehiclerequestRepository.save(vehicleRequest);
      VehicleRequestResponse response = vehicleRequestMapper.toVehicleRequestResponse(savedRequest);

      return APIResponse.success(response, "Update vehicle request successfully");
    } catch (GlobalException e) {
      throw e;
    } catch (Exception e) {
      System.out.println(e.getMessage());
      throw new GlobalException(ErrorCode.INVALID_CODE, e.getMessage());
    }
  }

  @Override
  public APIResponse<VehicleRequestResponse> delete(UUID id) {
    try {
      VehicleRequest vehicleRequest =
          vehiclerequestRepository
              .findById(id)
              .orElseThrow(
                  () -> new GlobalException(ErrorCode.NOT_FOUND, "Vehicle Request not found"));

      // Đánh dấu request là đã xóa
      vehicleRequest.setDeleted(true);

      // Đánh dấu tất cả items là đã xóa
      for (VehicleRequestItem item : vehicleRequest.getVehicleRequestItems()) {
        item.setDeleted(true);
      }

      // Lưu lại thay đổi
      vehiclerequestRepository.save(vehicleRequest);

      // Trả về response
      return APIResponse.success(
          vehicleRequestMapper.toVehicleRequestResponse(vehicleRequest),
          "Delete vehicle request successfully");

    } catch (GlobalException e) {
      throw e;
    } catch (Exception e) {
      throw new GlobalException(
          ErrorCode.INVALID_CODE, "Error deleting vehicle request: " + e.getMessage());
    }
  }

  @Override
  public APIResponse<VehicleRequestResponse> get(UUID id) {
    VehicleRequest request =
        vehiclerequestRepository
            .findById(id)
            .orElseThrow(
                () -> new GlobalException(ErrorCode.NOT_FOUND, "Vehicle Request not found"));

    Set<VehicleRequestItemResponse> itemResponses =
        request.getVehicleRequestItems().stream()
            .filter(item -> !item.isDeleted())
            .map(
                savedItem ->
                    VehicleRequestItemResponse.builder()
                        .id(savedItem.getId())
                        .vehicleId(savedItem.getVehicle().getId())
                        .vehicleStatus(savedItem.getVehicleStatus())
                        .color(savedItem.getColor())
                        .quantity(savedItem.getQuantity())
                        .unitPrice(savedItem.getUnitPrice())
                        .totalPrice(savedItem.getTotalPrice())
                        .build())
            .collect(Collectors.toSet());

    VehicleRequestResponse response = vehicleRequestMapper.toVehicleRequestResponse(request);
    response.setItems(itemResponses);

    return APIResponse.success(response, "Get vehicle request successfully");
  }

  @Override
  public APIResponse<PageResponse<VehicleRequestResponse>> getAll(Pageable pageable) {
    // 🔹 Lấy danh sách VehicleRequest chưa bị xóa, theo dealer hiện tại
    Page<VehicleRequest> page =
        vehiclerequestRepository.findAllByIsDeletedFalseAndDealer(
            AccountUtil.getCurrentUser().getDealer(), pageable);

    // 🔹 Ánh xạ sang DTO có phân trang
    PageResponse<VehicleRequestResponse> pageResponse =
        pageMapper.toPageResponse(page, vehicleRequestMapper::toVehicleRequestResponse);

    return APIResponse.success(pageResponse, "Get all vehicle requests successfully");
  }

  private VehicleRequestItem createVehicleRequestItem(VehicleRequestItemRequest request) {
    ElectricVehicle vehicle =
        electricVehicleRepository
            .findById(request.getVehicleId())
            .orElseThrow(() -> new GlobalException(ErrorCode.NOT_FOUND, "Vehicle not found"));

    return VehicleRequestItem.builder()
        .color(request.getColor())
        .quantity(request.getQuantity())
        .vehicleStatus(request.getVehicleStatus())
        .vehicle(vehicle)
        .build();
  }

  @Transactional
  public APIResponse<VehicleRequestResponse> approveVehicleRequest(
      UUID id, PaymentStatus paymentStatus) {
    VehicleRequest vehicleRequest =
        vehiclerequestRepository
            .findById(id)
            .orElseThrow(() -> new GlobalException(ErrorCode.NOT_FOUND, "Quotation not found"));
    vehicleRequest.setStatus(VehicleRequestStatus.APPROVED);
    VehicleRequest savedVehicleRequest = vehiclerequestRepository.save(vehicleRequest);
    saleOrderService.createSaleOrderFromVehicleRequest(vehicleRequest, paymentStatus);
    return APIResponse.success(
        vehicleRequestMapper.toVehicleRequestResponse(savedVehicleRequest),
        "Approve VehicleRequest successfully");
  }
}
