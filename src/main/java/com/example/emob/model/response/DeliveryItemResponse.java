package com.example.emob.model.response;

import com.example.emob.constant.DeliveryItemStatus;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class DeliveryItemResponse {
    UUID id;
    UUID vehicleId;
    DeliveryItemStatus status;
    String remarks;
}
