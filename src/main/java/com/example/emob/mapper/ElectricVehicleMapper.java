package com.example.emob.mapper;

import com.example.emob.entity.Dealer;
import com.example.emob.entity.ElectricVehicle;
import com.example.emob.entity.VehicleUnit;
import com.example.emob.model.request.ElectricVehicleRequest;
import com.example.emob.model.request.VehicleUnitRequest;
import com.example.emob.model.response.ElectricVehicleResponse;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring")
public interface ElectricVehicleMapper {
    ElectricVehicle toVehicle(ElectricVehicleRequest request);


    ElectricVehicleResponse toVehicleResponse(ElectricVehicle vehicle);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateVehicle(ElectricVehicleRequest request, @MappingTarget ElectricVehicle vehicle);

        @Mapping(target = "vinNumber", expression = "java(generateVin(vehicle.getModel()))")
        @Mapping(target = "status", constant = "IN_STOCK")
        @Mapping(target = "vehicle", source = "vehicle")
        VehicleUnit toVehicleUnit(VehicleUnitRequest request, ElectricVehicle vehicle);

        default String generateVin(String model) {
            String prefix = model.substring(0, Math.min(model.length(), 3)).toUpperCase();
            String randomPart = UUID.randomUUID().toString().replace("-", "").substring(0, 8).toUpperCase();

            return prefix + "-" + randomPart;
        }
}
