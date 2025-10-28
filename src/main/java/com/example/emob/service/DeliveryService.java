/* EMOB-2025 */
package com.example.emob.service;

import com.example.emob.constant.*;
import com.example.emob.entity.*;
import com.example.emob.exception.GlobalException;
import com.example.emob.mapper.DeliveryMapper;
import com.example.emob.mapper.PageMapper;
import com.example.emob.model.request.delivery.DeliveryRequest;
import com.example.emob.model.response.APIResponse;
import com.example.emob.model.response.DeliveryResponse;
import com.example.emob.model.response.PageResponse;
import com.example.emob.repository.*;
import com.example.emob.service.impl.IDelivery;
import com.example.emob.util.AccountUtil;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class DeliveryService implements IDelivery {
  @Autowired PageMapper pageMapper;

  @Autowired DeliveryRepository deliveryRepository;

  @Autowired SaleContractRepository contractRepository;

  @Autowired DeliveryMapper deliveryMapper;

  @Autowired VehicleUnitRepository vehicleUnitRepository;
  @Autowired InventoryRepository inventoryRepository;
  @Autowired CustomerRepository customerRepository;

  @Override
  @Transactional
  @PreAuthorize("hasRole('EVM_STAFF')")
  public APIResponse<DeliveryResponse> createDeliveryToDealer(DeliveryRequest request) {
    // 1️⃣ Xác thực hợp đồng đủ điều kiện
    SaleContract contract =
        contractRepository
            .findById(request.getContractId())
            .filter(
                c ->
                        c.getStatus().equals(ContractStatus.SIGNED)
                        && c.getSaleContractItems() != null
                        && !c.getSaleContractItems().isEmpty())
            .orElseThrow(
                () ->
                    new GlobalException(
                        ErrorCode.NOT_FOUND,
                        "Contract not found, not completed, or already assigned vehicles"));

    try {
      // 2️⃣ Khởi tạo entity Delivery từ mapper
      Delivery delivery = deliveryMapper.toDelivery(request);
      delivery.setSaleContract(contract);
      delivery.setCreateAt(LocalDateTime.now());
      delivery.setStatus(DeliveryStatus.IN_PROGRESS);

      // 3️⃣ Xác định danh sách xe cần giao (theo từng item trong contract)
      Set<VehicleUnit> vehiclesToDeliver =
          contract.getSaleContractItems().stream()
              .flatMap(
                  item -> {
                    int requiredQty = item.getQuantity();
                    System.out.println("requiredQty: "+ requiredQty);

                    // Lấy danh sách xe khả dụng theo số lượng cần
                    List<VehicleUnit> availableUnits = vehicleUnitRepository
                            .findTopNByInventoryAndVehicleAndColorIgnoreCaseAndStatus(
                                    inventoryRepository.findInventoryByIsCompanyTrue(),
                                    item.getVehicle(),
                                    item.getColor(),
                                    item.getVehicleStatus(),
                                    PageRequest.of(0, requiredQty) // limit = requiredQty
                            );

                    int foundQty = availableUnits.size();
                    int missingQty = requiredQty - foundQty;

                    if (missingQty > 0) {
                      throw new GlobalException(
                          ErrorCode.NOT_FOUND,
                          "Missing "
                              + missingQty
                              + " of "
                              + requiredQty
                              + " vehicle unit(s) for model: "
                              + item.getVehicle().getModel()
                              + ", color: "
                              + item.getColor()
                              + ", type: "
                              + item.getVehicleStatus());
                    }
                    System.out.println("size: "+ availableUnits.size());
                    return availableUnits.stream();
                  })
              .collect(Collectors.toSet());

      if (vehiclesToDeliver.isEmpty()) {
        throw new GlobalException(ErrorCode.DATA_INVALID, "No vehicle units found for delivery");
      }
      System.out.println("vehiclesToDeliver size: "+ vehiclesToDeliver.size());
      // 🔹 3.1 Gỡ inventory khỏi tất cả vehicle units (vì giao ra khỏi kho công ty)
      vehiclesToDeliver.forEach(vehicle -> vehicle.setInventory(null));

      // 4️⃣ Liên kết hai chiều giữa contract – delivery – vehicleUnits
      delivery.setVehicleUnits(vehiclesToDeliver);
      delivery.setQuantity(vehiclesToDeliver.size());
      contract.setDelivery(delivery);

      // 5️⃣ Lưu xuống DB
      Delivery savedDelivery = deliveryRepository.save(delivery);

      // 6️⃣ Map sang Response
      DeliveryResponse response = deliveryMapper.toDeliveryResponse(savedDelivery);
      return APIResponse.success(response, "Create Delivery Successfully");

    } catch (DataIntegrityViolationException ex) {
      throw new GlobalException(ErrorCode.DATA_INVALID, "Invalid delivery data");
    } catch (DataAccessException ex) {
      throw new GlobalException(ErrorCode.DB_ERROR, "Database error while saving delivery");
    } catch (Exception ex) {
      throw new GlobalException(ErrorCode.OTHER, "Unexpected error: " + ex.getMessage());
    }
  }

  @Override
  @Transactional
  @PreAuthorize("hasRole('DEALER_STAFF')")
  public APIResponse<DeliveryResponse> createDeliveryToCustomer(DeliveryRequest request) {
    // ===== 1️⃣ Lấy hợp đồng đã hoàn tất & có xe =====
    SaleContract contract =
        contractRepository
            .findById(request.getContractId())
            .filter(
                c ->
                    c.getStatus().equals(ContractStatus.SIGNED)
                        && c.getSaleContractItems() != null
                        && !c.getSaleContractItems().isEmpty())
            .orElseThrow(
                () ->
                    new GlobalException(
                        ErrorCode.NOT_FOUND,
                        "Contract not found, not completed, or has no vehicles"));

    try {
      // ===== 2️⃣ Map Delivery entity từ request =====
      Delivery delivery = deliveryMapper.toDelivery(request);
      delivery.setSaleContract(contract);
      delivery.setStatus(DeliveryStatus.IN_PROGRESS);
      delivery.setCreateAt(LocalDateTime.now());

      // ===== 3️⃣ Gom toàn bộ xe trong hợp đồng =====
      Set<VehicleUnit> vehicleUnits =
          contract.getSaleContractItems().stream()
              .filter(Objects::nonNull)
              .flatMap(item -> item.getVehicleUnits().stream())
              .collect(Collectors.toSet());

      if (vehicleUnits.isEmpty()) {
        throw new GlobalException(ErrorCode.DATA_INVALID, "No vehicles found in this contract");
      }

      // 🔹 3.1 Gỡ inventory khỏi tất cả xe (vì giao ra khỏi đại lý cho khách)
      vehicleUnits.forEach(vehicle -> vehicle.setInventory(null));

      // ===== 4️⃣ Liên kết hai chiều =====
      delivery.setVehicleUnits(vehicleUnits);
      delivery.setQuantity(vehicleUnits.size());
      contract.setDelivery(delivery);

      // ===== 5️⃣ Lưu xuống DB =====
      Delivery saved = deliveryRepository.save(delivery);

      // ===== 6️⃣ Trả về response =====
      DeliveryResponse response = deliveryMapper.toDeliveryResponse(saved);
      return APIResponse.success(response, "Create Delivery Successfully");

    } catch (DataIntegrityViolationException ex) {
      throw new GlobalException(ErrorCode.DATA_INVALID, "Invalid delivery data");
    } catch (DataAccessException ex) {
      throw new GlobalException(ErrorCode.DB_ERROR, "Database error while saving delivery");
    } catch (Exception ex) {
      throw new GlobalException(ErrorCode.OTHER, "Unexpected error: " + ex.getMessage());
    }
  }

  @Override
  public APIResponse<DeliveryResponse> getDelivery(UUID id) {
    Delivery delivery =
        deliveryRepository.findById(id).orElseThrow(() -> new GlobalException(ErrorCode.NOT_FOUND));
    DeliveryResponse deliveryResponse = deliveryMapper.toDeliveryResponse(delivery);
    return APIResponse.success(deliveryResponse, "View Delivery Successfully");
  }

  @Override
  @PreAuthorize("hasAnyRole('EVM_STAFF', 'DEALER_STAFF')")
  public APIResponse<Void> deleteDelivery(UUID id) {
    Delivery delivery =
        deliveryRepository.findById(id).orElseThrow(() -> new GlobalException(ErrorCode.NOT_FOUND));
    delivery.setDeleted(true);
    deliveryRepository.save(delivery);
    return APIResponse.success(null, "Delete Delivery Successfully");
  }

  // ===========================================
  // 🔹 1. Hãng xe xem tất cả delivery của đại lý
  // ===========================================
  @Override
  @PreAuthorize("hasAnyRole('EVM_STAFF', 'ADMIN')")
  public APIResponse<PageResponse<DeliveryResponse>> getAllDeliveriesOfDealers(
      List<DeliveryStatus> statuses, Pageable pageable) {
    Page<Delivery> page = deliveryRepository.findAllWithVehicleRequest(statuses, pageable);

    PageResponse<DeliveryResponse> response =
        pageMapper.toPageResponse(page, deliveryMapper::toDeliveryResponse);

    return APIResponse.success(response);
  }

  // ===========================================
  // 🔹 2. Đại lý xem delivery của khách hàng cụ thể
  // ===========================================
  @Override
  @PreAuthorize("hasAnyRole('DEALER_STAFF', 'MANAGER')")
  public APIResponse<PageResponse<DeliveryResponse>> getAllDeliveriesOfCurrentCustomer(
      UUID customerId, List<DeliveryStatus> statuses, Pageable pageable) {
    Dealer dealer = AccountUtil.getCurrentUser().getDealer();
    Customer customer =
        customerRepository
            .findById(customerId)
            .orElseThrow(() -> new GlobalException(ErrorCode.NOT_FOUND, "Customer not found"));

    Page<Delivery> page =
        deliveryRepository.findAllWithQuotationByDealerAndCustomer(
            dealer, customer, statuses, pageable);

    PageResponse<DeliveryResponse> response =
        pageMapper.toPageResponse(page, deliveryMapper::toDeliveryResponse);

    return APIResponse.success(response);
  }

  // ===========================================
  // 🔹 3. Đại lý xem tất cả delivery của chính mình
  // ===========================================
  @Override
  @PreAuthorize("hasAnyRole('DEALER_STAFF', 'MANAGER')")
  public APIResponse<PageResponse<DeliveryResponse>> getAllDeliveriesOfCurrentDealer(
      List<DeliveryStatus> statuses, Pageable pageable) {
    Dealer dealer = AccountUtil.getCurrentUser().getDealer();

    Page<Delivery> page =
        deliveryRepository.findAllWithVehicleRequestByDealerAndStatuses(dealer, statuses, pageable);

    PageResponse<DeliveryResponse> response =
        pageMapper.toPageResponse(page, deliveryMapper::toDeliveryResponse);

    return APIResponse.success(response);
  }

  // ===========================================
  // 🔹 4. Đại lý xem tất cả delivery đã báo giá (mọi khách hàng)
  // ===========================================
  @Override
  @PreAuthorize("hasAnyRole('DEALER_STAFF', 'MANAGER')")
  public APIResponse<PageResponse<DeliveryResponse>> getAllDeliveriesByCustomer(
      List<DeliveryStatus> statuses, Pageable pageable) {
    Dealer dealer = AccountUtil.getCurrentUser().getDealer();

    Page<Delivery> page =
        deliveryRepository.findAllWithQuotationByDealerAndStatuses(dealer, statuses, pageable);

    PageResponse<DeliveryResponse> response =
        pageMapper.toPageResponse(page, deliveryMapper::toDeliveryResponse);

    return APIResponse.success(response);
  }

  // ===========================================
  // 🔹 5. Lấy chi tiết 1 delivery
  // ===========================================
  @Override
  public APIResponse<DeliveryResponse> getDeliveryById(UUID deliveryId) {
    Delivery delivery =
        deliveryRepository
            .findById(deliveryId)
            .orElseThrow(() -> new GlobalException(ErrorCode.NOT_FOUND, "Delivery not found"));
    return APIResponse.success(deliveryMapper.toDeliveryResponse(delivery));
  }

  @Override
  @PreAuthorize("hasAnyRole('EVM_STAFF', 'DEALER_STAFF')")
  public APIResponse<DeliveryResponse> completeDelivery(UUID deliveryId) {
    Delivery delivery =
        deliveryRepository
            .findById(deliveryId)
            .orElseThrow(() -> new GlobalException(ErrorCode.NOT_FOUND, "Delivery not found"));

    if (delivery.getStatus() != DeliveryStatus.IN_PROGRESS) {
      throw new GlobalException(
          ErrorCode.DATA_INVALID, "Only deliveries in progress can be completed");
    }

    delivery.setStatus(DeliveryStatus.SUCCESS);
    Dealer dealer = null;
    if(delivery.getSaleContract().getSaleOrder().getVehicleRequest() != null){
      dealer  = delivery.getSaleContract().getSaleOrder().getVehicleRequest().getDealer();
    }

    // nếu giao cho đại lý thì cập nhật kho cho xe
    if (dealer != null) {
      delivery.getVehicleUnits().forEach(vehicle -> vehicle.setInventory(dealer.getInventory()));
    } else {
      // nếu giao xe cho khách thì chuyển thành đã bán
      delivery.getVehicleUnits().forEach(vehicle -> {
        vehicle.setStatus(VehicleStatus.SOLD);
        vehicle.setWarrantyStart(LocalDate.now());
        vehicle.setWarrantyEnd(LocalDate.now().plusYears(2));
      });
    }
    delivery.setCompletedAt(LocalDateTime.now());
    Delivery updatedDelivery = deliveryRepository.save(delivery);

    return APIResponse.success(
        deliveryMapper.toDeliveryResponse(updatedDelivery), "Delivery completed successfully");
  }
}
