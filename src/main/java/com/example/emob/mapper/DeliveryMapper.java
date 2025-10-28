/* EMOB-2025 */
package com.example.emob.mapper;

import com.example.emob.entity.Delivery;
import com.example.emob.entity.VehicleUnit;
import com.example.emob.model.request.delivery.DeliveryRequest;
import com.example.emob.model.response.DeliveryResponse;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface DeliveryMapper {
  @Mapping(target = "vehicleIds", expression = "java(mapVehicleIds(delivery.getVehicleUnits()))")
  DeliveryResponse toDeliveryResponse(Delivery delivery);

  Delivery toDelivery(DeliveryRequest request);

  default Set<UUID> mapVehicleIds(Set<VehicleUnit> vehicleUnits) {
    if (vehicleUnits == null) return new HashSet<>();
    return vehicleUnits.stream().map(VehicleUnit::getId).collect(Collectors.toSet());
  }
}
