package com.example.emob.mapper;

import com.example.emob.entity.Dealer;
import com.example.emob.entity.ElectricVehicle;
import com.example.emob.model.request.ElectricVehicleRequest;
import com.example.emob.model.response.ElectricVehicleResponse;
import org.mapstruct.*;

import java.util.Collections;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring")
public interface ElectricVehicleMapper {
    ElectricVehicle toVehicle(ElectricVehicleRequest request);


    ElectricVehicleResponse toVehicleResponse(ElectricVehicle vehicle);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateVehicle(ElectricVehicleRequest request, @MappingTarget ElectricVehicle vehicle);
}
