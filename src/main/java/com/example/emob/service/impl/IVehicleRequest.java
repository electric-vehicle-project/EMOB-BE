/* EMOB-2025 */
package com.example.emob.service.impl;

import com.example.emob.constant.VehicleRequestStatus;
import com.example.emob.model.request.vehicleRequest.VehicleRequestItemRequest;
import com.example.emob.model.request.vehicleRequest.VehicleRequestItemUpdateRequest;
import com.example.emob.model.request.vehicleRequest.VehicleRequestRequest;
import com.example.emob.model.response.APIResponse;
import com.example.emob.model.response.PageResponse;
import com.example.emob.model.response.vehicleRequest.VehicleRequestResponse;

import java.util.List;
import java.util.UUID;
import org.springframework.data.domain.Pageable;

public interface IVehicleRequest {
  APIResponse<VehicleRequestResponse> create(
      VehicleRequestRequest<VehicleRequestItemRequest> request);

  APIResponse<VehicleRequestResponse> update(
      UUID id, VehicleRequestRequest<VehicleRequestItemUpdateRequest> request);

  APIResponse<VehicleRequestResponse> delete(UUID id);

  APIResponse<VehicleRequestResponse> get(UUID id);

  APIResponse<PageResponse<VehicleRequestResponse>> getAll(Pageable pageable, String keyword, List<VehicleRequestStatus> status);
}
