/* EMOB-2025 */
package com.example.emob.mapper;

import com.example.emob.entity.DeliveryItem;
import com.example.emob.model.request.delivery.DeliveryItemRequest;
import com.example.emob.model.response.DeliveryItemResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface DeliveryItemMapper {
  @Mapping(target = "vehicleId", source = "vehicleUnit.id")
  DeliveryItemResponse toDeliveryItemResponse(DeliveryItem delivery);

  @Mapping(target = "vehicleUnit.id", source = "vehicleId") // map vehicleId → VehicleUnit
  @Mapping(target = "delivery", ignore = true) // delivery set ở service
  @Mapping(target = "createAt", expression = "java(java.time.LocalDateTime.now())")
  @Mapping(target = "updateAt", ignore = true)
  @Mapping(target = "confirmAt", ignore = true)
  DeliveryItem toDeliveryItem(DeliveryItemRequest request);
}
