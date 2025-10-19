package com.example.emob.model.request.quotation;

import com.example.emob.constant.VehicleStatus;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.UUID;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class QuotationItemUpdateRequest {
    UUID id;
    UUID vehicleId;
    UUID promotionId;
    VehicleStatus vehicleStatus;
    String color;
    int quantity;
}
