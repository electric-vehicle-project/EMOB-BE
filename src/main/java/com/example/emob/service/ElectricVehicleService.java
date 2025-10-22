/* EMOB-2025 */
package com.example.emob.service;

import com.example.emob.constant.ErrorCode;
import com.example.emob.constant.Role;
import com.example.emob.entity.*;
import com.example.emob.exception.GlobalException;
import com.example.emob.mapper.ElectricVehicleMapper;
import com.example.emob.mapper.PageMapper;
import com.example.emob.model.request.vehicle.ElectricVehiclePriceRequest;
import com.example.emob.model.request.vehicle.ElectricVehicleRequest;
import com.example.emob.model.request.vehicle.VehicleUnitRequest;
import com.example.emob.model.response.APIResponse;
import com.example.emob.model.response.ElectricVehicleResponse;
import com.example.emob.model.response.PageResponse;
import com.example.emob.model.response.VehicleUnitResponse;
import com.example.emob.repository.ElectricVehicleRepository;
import com.example.emob.repository.InventoryRepository;
import com.example.emob.repository.VehicleUnitRepository;
import com.example.emob.service.impl.IVehicle;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.IntStream;

import com.example.emob.util.AccountUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ElectricVehicleService implements IVehicle {

  @Autowired ElectricVehicleRepository vehicleRepository;

  @Autowired ElectricVehicleMapper vehicleMapper;

  @Autowired PageMapper pageMapper;

  @Autowired InventoryRepository inventoryRepository;

  @Autowired VehicleUnitRepository vehicleUnitRepository;
  @Autowired VehiclePriceRuleService vehiclePriceRuleService;
    @Autowired
    private ElectricVehicleRepository electricVehicleRepository;

  @Override
  public APIResponse<ElectricVehicleResponse> create(ElectricVehicleRequest request) {
    try {
      ElectricVehicle vehicle = vehicleMapper.toVehicle(request);
      vehicle.setCreatedAt(LocalDate.now());
      vehicleRepository.save(vehicle);

      return APIResponse.success(
          vehicleMapper.toVehicleResponse(vehicle), "Vehicle created successfully");

    } catch (Exception e) {
      throw new GlobalException(ErrorCode.INVALID_CODE);
    }
  }

  @Override
  public APIResponse<ElectricVehicleResponse> update(UUID id, ElectricVehicleRequest request) {
    try {
      ElectricVehicle vehicle =
          vehicleRepository
              .findById(id)
              .orElseThrow(() -> new GlobalException(ErrorCode.NOT_FOUND));

      vehicleMapper.updateVehicle(request, vehicle);
      vehicleRepository.save(vehicle);

      return APIResponse.success(
          vehicleMapper.toVehicleResponse(vehicle), "Vehicle updated successfully");

    } catch (Exception e) {
      throw new GlobalException(ErrorCode.INVALID_CODE);
    }
  }

  @Override
  public APIResponse<ElectricVehicleResponse> delete(UUID id) {
    try {
      ElectricVehicle vehicle =
          vehicleRepository
              .findById(id)
              .orElseThrow(() -> new GlobalException(ErrorCode.NOT_FOUND));

      vehicle.setDeleted(true);
      vehicleRepository.save(vehicle);

      return APIResponse.success(
          vehicleMapper.toVehicleResponse(vehicle), "Vehicle deleted successfully");

    } catch (Exception e) {
      throw new GlobalException(ErrorCode.INVALID_CODE);
    }
  }

  @Override
  public APIResponse<ElectricVehicleResponse> get(UUID id) {
    try {
      ElectricVehicle vehicle =
          vehicleRepository
              .findById(id)
              .orElseThrow(() -> new GlobalException(ErrorCode.NOT_FOUND));

      return APIResponse.success(vehicleMapper.toVehicleResponse(vehicle));

    } catch (Exception e) {
      throw new GlobalException(ErrorCode.INVALID_CODE);
    }
  }

  @Override
  public APIResponse<PageResponse<ElectricVehicleResponse>> getAll(Pageable pageable) {
    try {
      Page<ElectricVehicle> page = vehicleRepository.findAllByIsDeletedFalse(pageable);
      PageResponse<ElectricVehicleResponse> response =
          pageMapper.toPageResponse(page, vehicleMapper::toVehicleResponse);
      return APIResponse.success(response);
    } catch (Exception e) {
      throw new GlobalException(ErrorCode.INVALID_CODE);
    }
  }

  @Override
  @Transactional
  public APIResponse<List<VehicleUnitResponse>> createBulkVehicles(VehicleUnitRequest request) {

    // Tìm mẫu xe
    ElectricVehicle vehicle =
        vehicleRepository
            .findById(request.getVehicleId())
            .orElseThrow(() -> new GlobalException(ErrorCode.NOT_FOUND));
    if (vehicle.getRetailPrice().compareTo(BigDecimal.ZERO) == 0
        || vehicle.getImportPrice().compareTo(BigDecimal.ZERO) == 0) {
      throw new GlobalException(ErrorCode.VEHICLE_PRICE_NOT_SET);
    }
    // Tìm kho của hãng
    Inventory inventory =
        inventoryRepository
            .findInventoryByIsCompanyTrue();
    if (inventory == null) {
        throw new GlobalException(ErrorCode.NOT_FOUND, "Inventory for company not found.");
    }
    // tìm VehiclePriceRule

    // Tạo danh sách VehicleUnit
    List<VehicleUnit> units =
        IntStream.range(0, request.getQuantity())
            .mapToObj(
                i -> {
                  VehiclePriceRule rule = vehiclePriceRuleService.getRule(request.getStatus());
                  double multiplier = rule.getMultiplier();
                  VehicleUnit unit = vehicleMapper.toVehicleUnit(request, vehicle);
                  unit.setInventory(inventory);
                  unit.setColor(request.getColor());
                  unit.setStatus(request.getStatus());
                  unit.setPrice(vehicle.getImportPrice().multiply(BigDecimal.valueOf(multiplier)));
                  return unit;
                })
            .toList();

    // Lưu tất cả VehicleUnit
    List<VehicleUnit> savedUnits = vehicleUnitRepository.saveAll(units);

    // Cập nhật tổng số lượng trong kho
    inventory.setQuantity(inventory.getQuantity() + savedUnits.size());
    inventoryRepository.save(inventory);

    // Mapping sang Response
    List<VehicleUnitResponse> responses =
        savedUnits.stream()
            .map(
                unit -> {
                  VehicleUnitResponse resp = vehicleMapper.toVehicleUnitResponse(unit);
                  resp.setVehicleUnitId(unit.getId());
                  resp.setColor(unit.getColor()); // lấy color trực tiếp từ entity
                  return resp;
                })
            .toList();

    return APIResponse.success(
        responses,
        "Created " + savedUnits.size() + " vehicle units and linked to company inventory.");
  }

  @Override
  public APIResponse<ElectricVehicleResponse> updatePrices(
      UUID id, ElectricVehiclePriceRequest request) {
    try {
      ElectricVehicle vehicle =
          vehicleRepository
              .findById(id)
              .orElseThrow(() -> new GlobalException(ErrorCode.NOT_FOUND));

      vehicle.setImportPrice(request.getImportPrice());
      vehicle.setRetailPrice(request.getRetailPrice());
      vehicleRepository.save(vehicle);

      ElectricVehicleResponse response = vehicleMapper.toVehicleResponse(vehicle);
      return APIResponse.success(response, "Update price successfully");

    } catch (Exception e) {
      throw new GlobalException(ErrorCode.INVALID_CODE);
    }
  }

  @Override
  public APIResponse<VehicleUnitResponse> getVehicleUnit(UUID id) {
    Account account = AccountUtil.getCurrentUser();
    Inventory inventory;
    if (account.getDealer() == null) { // admin || evm_staff
      inventory = inventoryRepository.findInventoryByIsCompanyTrue();
      if (inventory == null) {
        throw new GlobalException(ErrorCode.NOT_FOUND, "Inventory for company not found.");
      }
    } else { // Manager || dealer_staff
      inventory = account.getDealer().getInventory();
      if  (inventory == null) {
        throw new GlobalException(ErrorCode.NOT_FOUND, "Inventory for dealer not found.");
      }
    }
    VehicleUnit vehicleUnit = vehicleUnitRepository.findById(id)
        .orElseThrow(() -> new GlobalException(ErrorCode.NOT_FOUND, "Vehicle unit not found."));
    try {
      // Kiểm tra vehicleUnit có thuộc inventory này không
      Optional<VehicleUnit> vehicleInInventory = vehicleUnitRepository.findByIdAndInventory(vehicleUnit.getId(), inventory);
      if (vehicleInInventory.isEmpty()) {
        throw new GlobalException(ErrorCode.NOT_FOUND, "Vehicle unit not found in your inventory.");
      }
      VehicleUnitResponse vehicleUnitResponse = vehicleMapper.toVehicleUnitResponse(vehicleUnit);
      vehicleUnitResponse.setVehicleUnitId(vehicleUnit.getId());
      return APIResponse.success(vehicleUnitResponse, "Get vehicle unit successfully");
    } catch (Exception e) {
      throw new GlobalException(ErrorCode.INVALID_CODE);
    }
  }

  @Override
  public APIResponse<PageResponse<VehicleUnitResponse>> getAllVehicleUnits(Pageable pageable) {
    Account account = AccountUtil.getCurrentUser();
    Inventory inventory = null;
    if (account.getDealer() == null) { // admin || evm_staff
      inventory = inventoryRepository.findInventoryByIsCompanyTrue();
      if (inventory == null) {
        throw new GlobalException(ErrorCode.NOT_FOUND, "Inventory for company not found.");
      }
    } else { // Manager || dealer_staff
      inventory = account.getDealer().getInventory();
      if  (inventory == null) {
        throw new GlobalException(ErrorCode.NOT_FOUND, "Inventory for dealer not found.");
      }
    }
    try {
      Page<VehicleUnit> page = vehicleUnitRepository.findAllByInventory(inventory, pageable);
      PageResponse<VehicleUnitResponse> response =
          pageMapper.toPageResponse(page, vehicleMapper::toVehicleUnitResponse);
      return APIResponse.success(response);
    } catch (Exception e) {
      throw new GlobalException(ErrorCode.INVALID_CODE);
    }
  }

  @Override
  public APIResponse<PageResponse<VehicleUnitResponse>> getAllVehicleUnitsByModelId(UUID modelId, Pageable pageable) {
    ElectricVehicle electricVehicle = electricVehicleRepository.findById(modelId).orElseThrow(() -> new GlobalException(ErrorCode.NOT_FOUND, "Electric vehicle model not found."));
    Account account = AccountUtil.getCurrentUser();
    Inventory inventory = null;
    if (account.getDealer() == null) { // admin || evm_staff
      inventory = inventoryRepository.findInventoryByIsCompanyTrue();
      if (inventory == null) {
        throw new GlobalException(ErrorCode.NOT_FOUND, "Inventory for company not found.");
      }
    } else { // Manager || dealer_staff
      inventory = account.getDealer().getInventory();
      if  (inventory == null) {
        throw new GlobalException(ErrorCode.NOT_FOUND, "Inventory for dealer not found.");
      }
    }
    try {
      Page<VehicleUnit> page = vehicleUnitRepository.findAllByVehicleAndInventory(electricVehicle ,inventory, pageable);
      PageResponse<VehicleUnitResponse> response =
              pageMapper.toPageResponse(page, vehicleMapper::toVehicleUnitResponse);
      return APIResponse.success(response);
    } catch (Exception e) {
      throw new GlobalException(ErrorCode.INVALID_CODE);
    }
  }


  //  @Transactional
  //  @Override
  //  public void autoUpdateVehiclePrices(BigDecimal basePrice) {
  //    List<VehicleUnit> vehicles = vehicleUnitRepository.findAll();
  //    for (VehicleUnit v : vehicles) {
  //      // Lấy rule theo enum
  //      if(v.getStatus().equals(VehicleStatus.SOLD) ||
  // v.getStatus().equals(VehicleStatus.RESERVED)){
  //        continue;
  //      }
  //      VehiclePriceRule rule = vehiclePriceRuleService.getRule(v.getStatus());
  //      double multiplier = rule.getMultiplier();
  //
  //      BigDecimal finalPrice = basePrice.multiply(BigDecimal.valueOf(multiplier));
  //      v.setPrice(finalPrice);
  //      vehicleUnitRepository.save(v);
  //    }
  //  }


}
