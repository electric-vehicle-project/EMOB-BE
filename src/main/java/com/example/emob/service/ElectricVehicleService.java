package com.example.emob.service;

import com.example.emob.constant.ErrorCode;
import com.example.emob.constant.InventoryStatus;
import com.example.emob.constant.VehicleStatus;
import com.example.emob.entity.ElectricVehicle;
import com.example.emob.entity.Inventory;
import com.example.emob.entity.InventoryItem;
import com.example.emob.entity.VehicleUnit;
import com.example.emob.exception.GlobalException;
import com.example.emob.mapper.ElectricVehicleMapper;
import com.example.emob.mapper.PageMapper;
import com.example.emob.model.request.ElectricVehicleRequest;
import com.example.emob.model.request.VehicleUnitRequest;
import com.example.emob.model.response.APIResponse;
import com.example.emob.model.response.ElectricVehicleResponse;
import com.example.emob.model.response.PageResponse;
import com.example.emob.repository.ElectricVehicleRepository;
import com.example.emob.repository.InventoryItemRepository;
import com.example.emob.repository.InventoryRepository;
import com.example.emob.repository.VehicleUnitRepository;
import com.example.emob.service.iml.IVehicle;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Service
public class ElectricVehicleService implements IVehicle {
    @Autowired
    ElectricVehicleRepository vehicleRepository;
    @Autowired
    ElectricVehicleMapper vehicleMapper;
    @Autowired
    PageMapper pageMapper;
    @Autowired
    InventoryRepository inventoryRepository;
    @Autowired
    VehicleUnitRepository vehicleUnitRepository;
    @Autowired
    InventoryItemRepository inventoryItemRepository;

    @Override
    public APIResponse<ElectricVehicleResponse> create(ElectricVehicleRequest request) {
        try{
            ElectricVehicle vehicle = vehicleMapper.toVehicle(request);
            vehicle.setCreatedAt(LocalDate.now());
            vehicleRepository.save(vehicle);

            return APIResponse.success(vehicleMapper.toVehicleResponse(vehicle), "Vehicle created successfully");
        } catch (Exception e){
            System.out.println(e.getMessage());
            throw  new GlobalException(ErrorCode.DATA_INVALID);
        }

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




    @Transactional
    public APIResponse<List<VehicleUnit>> createBulkVehicles(VehicleUnitRequest request) {
        //Tìm ElectricVehicle
        ElectricVehicle vehicle = vehicleRepository.findById(request.getVehicleId())
                .orElseThrow(() -> new GlobalException(ErrorCode.NOT_FOUND));

        //Tìm kho của hãng xe
        Inventory inventory = inventoryRepository.findInventoryByIsCompanyTrue()
                .orElseThrow(() -> new GlobalException(ErrorCode.NOT_FOUND));

//        tính tổng hàng lại
        int total = inventory.getTotalQuantity() + request.getQuantity();
        inventory.setTotalQuantity(total);

        //tạo danh sách VehicleUnit
        List<VehicleUnit> units = IntStream.range(0, request.getQuantity())
                .mapToObj(i -> vehicleMapper.toVehicleUnit(request, vehicle))
                .collect(Collectors.toList());

        List<VehicleUnit> savedUnits = vehicleUnitRepository.saveAll(units);

        //check xem trong kho có mẫu đó chưa
        Optional<InventoryItem> existingItemOpt = inventoryItemRepository
                .findByInventoryAndVehicle(inventory, vehicle);
        InventoryItem item;
        if (existingItemOpt.isPresent()) {
            item = existingItemOpt.get();
            item.setQuantity(item.getQuantity() + request.getQuantity());
        } else {
            item = InventoryItem.builder()
                    .inventory(inventory)
                    .quantity(request.getQuantity())
                    .vehicle(vehicle)
                    .status(InventoryStatus.IN_STOCK)
                    .build();

        }
        inventoryItemRepository.save(item);


        // Trả về kết quả
        return APIResponse.success(savedUnits,   "Created " + savedUnits.size() + " vehicle units and linked to company inventory.");
    }



}
