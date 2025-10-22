/* EMOB-2025 */
package com.example.emob.mapper;

import com.example.emob.entity.VehicleRequest;
import com.example.emob.entity.VehicleRequestItem;
import com.example.emob.model.request.vehicleRequest.VehicleRequestRequest;
import com.example.emob.model.response.vehicleRequest.VehicleRequestItemResponse;
import com.example.emob.model.response.vehicleRequest.VehicleRequestResponse;
import java.util.Collections;
import java.util.Set;
import java.util.stream.Collectors;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface VehicleRequestMapper {

  VehicleRequest toVehicleRequest(VehicleRequestRequest request);

  @Mapping(source = "dealer.id", target = "dealerId")
  @Mapping(source = "status", target = "status")
  VehicleRequestResponse toVehicleRequestResponse(VehicleRequest request);

  VehicleRequestItemResponse toVehicleRequestItemResponse(VehicleRequestItem item);

  default Set<VehicleRequestItemResponse> filterActiveItems(Set<VehicleRequestItem> items) {
    if (items == null) return Collections.emptySet();
    return items.stream()
        .filter(item -> !Boolean.TRUE.equals(item.isDeleted()))
        .map(this::toVehicleRequestItemResponse)
        .collect(Collectors.toSet());
  }

  @org.mapstruct.AfterMapping
  default void afterMapping(
      @org.mapstruct.MappingTarget VehicleRequestResponse response, VehicleRequest vehicleRequest) {
    response.setItems(filterActiveItems(vehicleRequest.getVehicleRequestItems()));
  }
}
