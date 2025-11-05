/* EMOB-2025 */
package com.example.emob.service;

import com.example.emob.constant.ErrorCode;
import com.example.emob.constant.Region;
import com.example.emob.constant.VehicleStatus;
import com.example.emob.constant.VehicleType;
import com.example.emob.entity.*;
import com.example.emob.exception.GlobalException;
import com.example.emob.mapper.ElectricVehicleMapper;
import com.example.emob.mapper.PageMapper;
import com.example.emob.model.request.AIRequest.AIVehicleRequest;
import com.example.emob.model.request.AIRequest.DemandForecastRequest;
import com.example.emob.model.request.vehicle.ElectricVehiclePriceRequest;
import com.example.emob.model.request.vehicle.ElectricVehicleRequest;
import com.example.emob.model.request.vehicle.VehicleUnitRequest;
import com.example.emob.model.response.*;
import com.example.emob.repository.ElectricVehicleRepository;
import com.example.emob.repository.InventoryRepository;
import com.example.emob.repository.VehicleRequestRepository;
import com.example.emob.repository.VehicleUnitRepository;
import com.example.emob.service.impl.IVehicle;
import com.example.emob.util.AccountUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
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
  @Autowired private ElectricVehicleRepository electricVehicleRepository;
  @Autowired private VehicleRequestRepository vehicleRequestRepository;
  @Autowired private AIService aiService;
  @Autowired private ObjectMapper objectMapper;

  @Override
  public APIResponse<ElectricVehicleResponse> create(ElectricVehicleRequest request) {
    try {
      ElectricVehicle vehicle = vehicleMapper.toVehicle(request);
      vehicle.setCreatedAt(LocalDate.now());
      vehicle.setBrand(request.getBrand());
      vehicle.setModel(request.getModel());
      ElectricVehicle saved = vehicleRepository.save(vehicle);

      ElectricVehicleResponse response = vehicleMapper.toVehicleResponse(vehicle);
      response.setBrand(saved.getBrand());
      response.setModel(saved.getModel());
      return APIResponse.success(response, "Vehicle created successfully");

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
  public APIResponse<PageResponse<ElectricVehicleResponse>> getAll(
      Pageable pageable, String keyword, List<VehicleType> type) {
    try {
      Page<ElectricVehicle> page = vehicleRepository.searchAndFilter(keyword, type, pageable);

      PageResponse<ElectricVehicleResponse> response =
          pageMapper.toPageResponse(page, vehicleMapper::toVehicleResponse);

      return APIResponse.success(response, "Get all electric vehicles successfully");
    } catch (DataAccessException ex) {
      throw new GlobalException(ErrorCode.DB_ERROR, "Database error while fetching vehicles");
    } catch (Exception ex) {
      throw new GlobalException(ErrorCode.OTHER, "Unexpected error occurred");
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
    Inventory inventory = inventoryRepository.findInventoryByIsCompanyTrue();
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
                  unit.setVinNumber(generateVin(vehicle.getModel()));
                  unit.setStatus(request.getStatus());
                  unit.setPrice(vehicle.getRetailPrice().multiply(BigDecimal.valueOf(multiplier)));
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
      if (inventory == null) {
        throw new GlobalException(ErrorCode.NOT_FOUND, "Inventory for dealer not found.");
      }
    }
    VehicleUnit vehicleUnit =
        vehicleUnitRepository
            .findById(id)
            .orElseThrow(() -> new GlobalException(ErrorCode.NOT_FOUND, "Vehicle unit not found."));
    try {
      // Kiểm tra vehicleUnit có thuộc inventory này không
      Optional<VehicleUnit> vehicleInInventory =
          vehicleUnitRepository.findByIdAndInventory(vehicleUnit.getId(), inventory);
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
  public APIResponse<PageResponse<VehicleUnitResponse>> getAllVehicleUnits(
      Pageable pageable, String keyword, List<VehicleStatus> status) {
    Account account = AccountUtil.getCurrentUser();
    Inventory inventory = null;
    if (account.getDealer() == null) { // admin || evm_staff
      inventory = inventoryRepository.findInventoryByIsCompanyTrue();
      if (inventory == null) {
        throw new GlobalException(ErrorCode.NOT_FOUND, "Inventory for company not found.");
      }
    } else { // Manager || dealer_staff
      inventory = account.getDealer().getInventory();
      if (inventory == null) {
        throw new GlobalException(ErrorCode.NOT_FOUND, "Inventory for dealer not found.");
      }
    }
    try {
      Page<VehicleUnit> page =
          vehicleUnitRepository.searchAndFilter(inventory, keyword, status, pageable);

      PageResponse<VehicleUnitResponse> response =
          pageMapper.toPageResponse(page, vehicleMapper::toVehicleUnitResponse);

      return APIResponse.success(response);
    } catch (Exception e) {
      throw new GlobalException(ErrorCode.INVALID_CODE);
    }
  }

  @Override
  public APIResponse<PageResponse<VehicleUnitResponse>> getAllVehicleUnitsByModelId(
      UUID modelId, Pageable pageable) {
    ElectricVehicle electricVehicle =
        electricVehicleRepository
            .findById(modelId)
            .orElseThrow(
                () ->
                    new GlobalException(ErrorCode.NOT_FOUND, "Electric vehicle model not found."));
    Account account = AccountUtil.getCurrentUser();
    Inventory inventory = null;
    if (account.getDealer() == null) { // admin || evm_staff
      inventory = inventoryRepository.findInventoryByIsCompanyTrue();
      if (inventory == null) {
        throw new GlobalException(ErrorCode.NOT_FOUND, "Inventory for company not found.");
      }
    } else { // Manager || dealer_staff
      inventory = account.getDealer().getInventory();
      if (inventory == null) {
        throw new GlobalException(ErrorCode.NOT_FOUND, "Inventory for dealer not found.");
      }
    }
    try {
      Page<VehicleUnit> page =
          vehicleUnitRepository.findAllByVehicleAndInventory(electricVehicle, inventory, pageable);
      PageResponse<VehicleUnitResponse> response =
          pageMapper.toPageResponse(page, vehicleMapper::toVehicleUnitResponse);
      return APIResponse.success(response);
    } catch (Exception e) {
      throw new GlobalException(ErrorCode.INVALID_CODE);
    }
  }

  public List<VehicleCompareResponse> compare(UUID leftVehicleId, UUID rightVehicleId) {
    ElectricVehicle leftVehicle =
        vehicleRepository
            .findById(leftVehicleId)
            .orElseThrow(() -> new GlobalException(ErrorCode.NOT_FOUND, "Left vehicle not found."));
    ElectricVehicle rightVehicle =
        vehicleRepository
            .findById(rightVehicleId)
            .orElseThrow(
                () -> new GlobalException(ErrorCode.NOT_FOUND, "Right vehicle not found."));
    List<VehicleCompareResponse> compareResponses = new ArrayList<>();
    // ========== THẤP HƠN TỐT HƠN ==========
    compareResponses.add(
        buildRow(
            "importPrice",
            toDouble(leftVehicle.getImportPrice()),
            toDouble(rightVehicle.getImportPrice()),
            /*lowerIsBetter*/ true));
    compareResponses.add(
        buildRow(
            "retailPrice",
            toDouble(leftVehicle.getRetailPrice()),
            toDouble(rightVehicle.getRetailPrice()),
            true));
    compareResponses.add(
        buildRow(
            "chargeTimeHr",
            toDouble(leftVehicle.getChargeTimeHr()),
            toDouble(rightVehicle.getChargeTimeHr()),
            true));
    compareResponses.add(
        buildRow(
            "weightKg",
            toDouble(leftVehicle.getWeightKg()),
            toDouble(rightVehicle.getWeightKg()),
            true));

    // ========== CAO HƠN TỐT HƠN ==========
    compareResponses.add(
        buildRow(
            "batteryKwh",
            toDouble(leftVehicle.getBatteryKwh()),
            toDouble(rightVehicle.getBatteryKwh()),
            /*lowerIsBetter*/ false));
    compareResponses.add(
        buildRow(
            "rangeKm",
            toDouble(leftVehicle.getRangeKm()),
            toDouble(rightVehicle.getRangeKm()),
            false));
    compareResponses.add(
        buildRow(
            "powerKw",
            toDouble(leftVehicle.getPowerKw()),
            toDouble(rightVehicle.getPowerKw()),
            false));
    compareResponses.add(
        buildRow(
            "topSpeedKmh",
            toDouble(leftVehicle.getTopSpeedKmh()),
            toDouble(rightVehicle.getTopSpeedKmh()),
            false));

    return compareResponses;
  }

  /**
   * @param keyName tên trường (ví dụ "importPrice")
   * @param leftVal giá trị xe 1 (LEFT)
   * @param rightVal giá trị xe 2 (RIGHT)
   * @param lowerIsBetter true nếu giá trị thấp hơn là tốt hơn
   */
  private VehicleCompareResponse buildRow(
      String keyName, Double leftVal, Double rightVal, boolean lowerIsBetter) {
    // Chuẩn hoá null -> 0 để có chênh lệch
    double l = (leftVal == null) ? 0d : leftVal;
    double r = (rightVal == null) ? 0d : rightVal;

    double rawDelta = l - r; // chênh lệch LEFT - RIGHT
    double magnitude = Math.abs(rawDelta); // độ lớn dương
    boolean different = Double.compare(l, r) != 0;

    String betterFor = null;
    if (different) {
      int cmp = Double.compare(l, r);
      if (lowerIsBetter) {
        // thấp hơn tốt hơn
        betterFor = (cmp < 0) ? "LEFT" : "RIGHT";
      } else {
        // cao hơn tốt hơn
        betterFor = (cmp > 0) ? "LEFT" : "RIGHT";
      }
    }

    // vehicleValue lấy độ lớn dương rồi đặt dấu theo "tính chất tốt/xấu" của chỉ số
    double signedDelta = (lowerIsBetter ? -1 : 1) * magnitude;

    VehicleCompareResponse res = new VehicleCompareResponse();
    res.setKeyName(keyName);
    res.setVehicleValue((float) signedDelta); // ví dụ: -123.4 nếu "thấp hơn tốt hơn"
    res.setDifferent(different);
    res.setBetterFor(betterFor);
    return res;
  }

  private static Double toDouble(BigDecimal n) {
    return (n == null) ? null : n.doubleValue();
  }

  private static Double toDouble(Number n) {
    return (n == null) ? null : n.doubleValue();
  }

  private String generateVin(String model) {
    String prefix = model.substring(0, Math.min(model.length(), 3)).toUpperCase();
    String randomPart = UUID.randomUUID().toString().replace("-", "").substring(0, 8).toUpperCase();

    return prefix + "-" + randomPart;
  }

  public List<DemandForecastRequest> createDemandForecasts() {
    LocalDateTime threeMonthsAgo = LocalDateTime.now().minusMonths(3);
    List<Object[]> rows = vehicleRequestRepository.findSignedRequestsRaw(threeMonthsAgo);

    // ✅ Nhóm theo country + region
    Map<String, List<Object[]>> groupedByRegion =
        rows.stream().collect(Collectors.groupingBy(r -> r[0] + "|" + r[1]));

    return groupedByRegion.entrySet().stream()
        .map(
            entry -> {
              String[] key = entry.getKey().split("\\|");
              String country = key[0];
              Region region = Region.valueOf(key[1]);

              // ✅ Nhóm tiếp theo model
              Map<String, List<Object[]>> groupedByModel =
                  entry.getValue().stream().collect(Collectors.groupingBy(r -> (String) r[2]));

              Set<AIVehicleRequest> vehicles =
                  groupedByModel.entrySet().stream()
                      .map(
                          modelEntry -> {
                            String modelName = modelEntry.getKey();

                            // ✅ Map ra list color + quantity
                            List<Map<String, Object>> dataList =
                                modelEntry.getValue().stream()
                                    .map(
                                        r ->
                                            Map.of(
                                                "color",
                                                r[3],
                                                "totalRequests",
                                                ((Number) r[4]).longValue()))
                                    .toList();

                            // ✅ Tạo đối tượng vehicle
                            AIVehicleRequest ai = new AIVehicleRequest();
                            ai.setModelName(modelName);
                            ai.setData(dataList);
                            return ai;
                          })
                      .collect(Collectors.toSet());

              // ✅ Build DemandForecastRequest
              DemandForecastRequest forecast = new DemandForecastRequest();
              forecast.setCountry(country);
              forecast.setRegion(region);
              forecast.setVehicles(vehicles);
              forecast.setTimeRange("last_3_months");
              return forecast;
            })
        .toList();
  }

  public APIResponse<?> getDemandForecastFromAI() {
    try {
      // B1: Lấy dữ liệu 3 tháng gần nhất
      List<DemandForecastRequest> requests = createDemandForecasts();

      // B2: Gọi AI để dự báo nhu cầu
      String aiResponse = aiService.getAIResponse(requests);

      // B3: Parse JSON string -> Object (tránh bị trả về dạng String)
      Object jsonObject = objectMapper.readValue(aiResponse, Object.class);

      // B4: Trả về response chuẩn
      return APIResponse.success(jsonObject, "AI demand forecast retrieved successfully");

    } catch (Exception e) { // ✅ bắt được tất cả checked + runtime exceptions
      return APIResponse.error(400, "Failed to get AI demand forecast: " + e.getMessage());
    }
  }
}
