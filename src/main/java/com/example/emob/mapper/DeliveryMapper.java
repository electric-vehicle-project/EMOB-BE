package com.example.emob.mapper;

import com.example.emob.entity.Delivery;
import com.example.emob.model.request.delivery.DeliveryRequest;
import com.example.emob.model.response.DeliveryResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface DeliveryMapper {
    // @Mapping(source = "id", target = "id")
    // DeliveryResponse toDeliveryResponse (Delivery delivery);

    Delivery toDelivery (DeliveryRequest request);

}
