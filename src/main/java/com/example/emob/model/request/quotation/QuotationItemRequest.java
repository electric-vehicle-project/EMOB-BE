package com.example.emob.model.request.quotation;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.UUID;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class QuotationItemRequest {
    UUID vehicleId;
    String color;
    int quantity;
}
