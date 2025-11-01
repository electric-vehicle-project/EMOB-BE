/* EMOB-2025 */
package com.example.emob.mapper;

import com.example.emob.entity.ElectricVehicle;
import com.example.emob.entity.VehicleUnit;
import com.example.emob.model.request.vehicle.ElectricVehicleRequest;
import com.example.emob.model.request.vehicle.VehicleUnitRequest;
import com.example.emob.model.response.ElectricVehicleResponse;
import com.example.emob.model.response.VehicleUnitResponse;
import org.mapstruct.*;

@Mapper(componentModel = "spring")
public interface ElectricVehicleMapper {
  ElectricVehicle toVehicle(ElectricVehicleRequest request);

  @Mapping(target = "brand", source = "brand")
  @Mapping(target = "model", source = "model")
  ElectricVehicleResponse toVehicleResponse(ElectricVehicle vehicle);

  @Mapping(target = "vehicleUnitId", source = "id")
  VehicleUnitResponse toVehicleUnitResponse(VehicleUnit unit);

  @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
  void updateVehicle(ElectricVehicleRequest request, @MappingTarget ElectricVehicle vehicle);

  @Mapping(target = "vehicle", source = "vehicle")
  VehicleUnit toVehicleUnit(VehicleUnitRequest request, ElectricVehicle vehicle);
}
