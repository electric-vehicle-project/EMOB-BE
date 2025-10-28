/* EMOB-2025 */
package com.example.emob.service.impl;

import com.example.emob.constant.VehicleStatus;
import com.example.emob.constant.VehicleType;
import com.example.emob.model.request.vehicle.ElectricVehiclePriceRequest;
import com.example.emob.model.request.vehicle.ElectricVehicleRequest;
import com.example.emob.model.request.vehicle.VehicleUnitRequest;
import com.example.emob.model.response.APIResponse;
import com.example.emob.model.response.ElectricVehicleResponse;
import com.example.emob.model.response.PageResponse;
import com.example.emob.model.response.VehicleUnitResponse;
import java.util.List;
import java.util.UUID;
import org.springframework.data.domain.Pageable;

public interface IVehicle {
  APIResponse<ElectricVehicleResponse> create(ElectricVehicleRequest request);

  APIResponse<ElectricVehicleResponse> update(UUID id, ElectricVehicleRequest request);

  APIResponse<ElectricVehicleResponse> delete(UUID id);

  APIResponse<ElectricVehicleResponse> get(UUID id);

  APIResponse<PageResponse<ElectricVehicleResponse>> getAll(Pageable pageable, String keyword, List<VehicleType> type);

  APIResponse<List<VehicleUnitResponse>> createBulkVehicles(VehicleUnitRequest request);

  APIResponse<ElectricVehicleResponse> updatePrices(UUID id, ElectricVehiclePriceRequest request);

  //  void  autoUpdateVehiclePrices(Double basePrice);

  APIResponse<VehicleUnitResponse> getVehicleUnit(UUID id);

  APIResponse<PageResponse<VehicleUnitResponse>> getAllVehicleUnits(Pageable pageable, String keyword,
                                                                    List<VehicleStatus> status);

  APIResponse<PageResponse<VehicleUnitResponse>> getAllVehicleUnitsByModelId(
      UUID modelId, Pageable pageable);
}
