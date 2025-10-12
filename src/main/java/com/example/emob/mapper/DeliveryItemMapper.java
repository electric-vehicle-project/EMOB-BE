package com.example.emob.mapper;

import com.example.emob.entity.DeliveryItem;
import com.example.emob.model.request.delivery.DeliveryItemRequest;
import com.example.emob.model.response.DeliveryItemResponse;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface DeliveryItemMapper {
    DeliveryItemResponse toDeliveryItemResponse (DeliveryItem delivery);

    DeliveryItem toDeliveryItem (DeliveryItemRequest request);

}
