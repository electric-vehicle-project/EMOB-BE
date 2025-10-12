package com.example.emob.model.response;

import com.example.emob.constant.PromotionStatus;
import com.example.emob.entity.DeliveryItem;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;
import java.util.Set;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class DeliveryResponse {
    UUID id;
    Set<DeliveryItem> deliveryItems;
    LocalDateTime deliveryDate;
    int quantity;
    PromotionStatus status;
}
