package com.example.emob.mapper;

import com.example.emob.entity.Delivery;
import com.example.emob.model.request.delivery.DeliveryRequest;
import com.example.emob.model.response.DeliveryResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = {DeliveryItemMapper.class})
public interface DeliveryMapper {
     @Mapping(target = "deliveryItems", source = "deliveryItems")
     DeliveryResponse toDeliveryResponse (Delivery delivery);
     Delivery toDelivery (DeliveryRequest request);
}
