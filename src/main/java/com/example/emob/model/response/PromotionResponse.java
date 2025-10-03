package com.example.emob.model.response;

import com.example.emob.constant.PromotionScope;
import com.example.emob.constant.PromotionStatus;
import com.example.emob.constant.PromotionType;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class PromotionResponse {
    UUID promotionId;
    UUID dealerId;
    String name;
    String description;
    PromotionType type;
    float value;
    float minValue;
    LocalDateTime startDate;
    LocalDateTime endDate;
    PromotionScope scope;
    PromotionStatus status;
    LocalDateTime createAt;
}
