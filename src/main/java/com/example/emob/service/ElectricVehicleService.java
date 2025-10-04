package com.example.emob.service;

import com.example.emob.constant.ErrorCode;
import com.example.emob.entity.ElectricVehicle;
import com.example.emob.exception.GlobalException;
import com.example.emob.mapper.ElectricVehicleMapper;
import com.example.emob.mapper.PageMapper;
import com.example.emob.model.request.ElectricVehicleRequest;
import com.example.emob.model.response.APIResponse;
import com.example.emob.model.response.ElectricVehicleResponse;
import com.example.emob.model.response.PageResponse;
import com.example.emob.repository.ElectricVehicleRepository;
import com.example.emob.service.iml.IVehicle;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import org.springframework.data.domain.Pageable;
import java.time.LocalDate;
import java.util.UUID;

@Service
public class ElectricVehicleService implements IVehicle {
    @Autowired
    ElectricVehicleRepository vehicleRepository;
    @Autowired
    ElectricVehicleMapper vehicleMapper;
    @Autowired
    PageMapper pageMapper;

    @Override
    public APIResponse<ElectricVehicleResponse> create(ElectricVehicleRequest request) {
        ElectricVehicle vehicle = vehicleMapper.toVehicle(request);
        vehicle.setCreatedAt(LocalDate.now());
        vehicleRepository.save(vehicle);

        return APIResponse.success(vehicleMapper.toVehicleResponse(vehicle), "Vehicle created successfully");
    }

    @Override
    public APIResponse<ElectricVehicleResponse> update(UUID id, ElectricVehicleRequest request) {
        ElectricVehicle vehicle = vehicleRepository.findById(id)
                .orElseThrow(() -> new GlobalException(ErrorCode.NOT_FOUND));

        vehicleMapper.updateVehicle(request, vehicle);
        vehicleRepository.save(vehicle);

        return APIResponse.success(vehicleMapper.toVehicleResponse(vehicle), "Vehicle updated successfully");
    }

    @Override
    public APIResponse<ElectricVehicleResponse> delete(UUID id) {
        ElectricVehicle vehicle = vehicleRepository.findById(id)
                .orElseThrow(() -> new GlobalException(ErrorCode.NOT_FOUND));

        vehicleRepository.delete(vehicle); // hoặc set deleted flag nếu có field deleted
        return APIResponse.success(vehicleMapper.toVehicleResponse(vehicle), "Vehicle deleted successfully");
    }

    @Override
    public APIResponse<ElectricVehicleResponse> get(UUID id) {
        ElectricVehicle vehicle = vehicleRepository.findById(id)
                .orElseThrow(() -> new GlobalException(ErrorCode.NOT_FOUND));
        return APIResponse.success(vehicleMapper.toVehicleResponse(vehicle));
    }

    @Override
    public APIResponse<PageResponse<ElectricVehicleResponse>> getAll(Pageable pageable) {
        Page<ElectricVehicle> page = vehicleRepository.findAll(pageable);
        PageResponse<ElectricVehicleResponse> response =
                pageMapper.toPageResponse(page, vehicleMapper::toVehicleResponse);
        return APIResponse.success(response);
    }


}
