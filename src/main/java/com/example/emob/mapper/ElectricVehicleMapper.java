/* EMOB-2025 */
package com.example.emob.mapper;

import com.example.emob.entity.ElectricVehicle;
import com.example.emob.entity.VehicleUnit;
import com.example.emob.model.request.vehicle.ElectricVehicleRequest;
import com.example.emob.model.request.vehicle.VehicleUnitRequest;
import com.example.emob.model.response.ElectricVehicleResponse;
import com.example.emob.model.response.VehicleUnitResponse;
import java.util.UUID;
import org.mapstruct.*;

import java.util.UUID;

@Mapper(componentModel = "spring")
public interface ElectricVehicleMapper {
    ElectricVehicle toVehicle(ElectricVehicleRequest request);


    ElectricVehicleResponse toVehicleResponse(ElectricVehicle vehicle);

    VehicleUnitResponse toVehicleUnitResponse(VehicleUnit unit);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateVehicle(ElectricVehicleRequest request, @MappingTarget ElectricVehicle vehicle);

    @Mapping(target = "vinNumber", expression = "java(generateVin(vehicle.getModel()))")
    @Mapping(target = "vehicle", source = "vehicle")
    VehicleUnit toVehicleUnit(VehicleUnitRequest request, ElectricVehicle vehicle);

    default String generateVin(String model) {
        String prefix = model.substring(0, Math.min(model.length(), 3)).toUpperCase();
        String randomPart =
                UUID.randomUUID().toString().replace("-", "").substring(0, 8).toUpperCase();

        return prefix + "-" + randomPart;
    }
}