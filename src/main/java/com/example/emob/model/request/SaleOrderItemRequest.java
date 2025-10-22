package com.example.emob.model.request;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.UUID;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class SaleOrderItemRequest {
    UUID itemsId;
    UUID promotionId;
}
