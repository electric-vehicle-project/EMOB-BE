package com.example.emob.service.iml;

import com.example.emob.model.request.ElectricVehicleRequest;
import com.example.emob.model.response.APIResponse;
import com.example.emob.model.response.ElectricVehicleResponse;
import com.example.emob.model.response.PageResponse;

import org.springframework.data.domain.Pageable;
import java.util.UUID;

public interface IVehicle {
    APIResponse<ElectricVehicleResponse> create(ElectricVehicleRequest request);

    APIResponse<ElectricVehicleResponse> update(UUID id, ElectricVehicleRequest request);

    APIResponse<ElectricVehicleResponse> delete(UUID id);

    APIResponse<ElectricVehicleResponse> get(UUID id);

    APIResponse<PageResponse<ElectricVehicleResponse>> getAll(Pageable pageable);
}
