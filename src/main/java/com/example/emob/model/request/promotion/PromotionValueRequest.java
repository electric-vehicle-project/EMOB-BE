package com.example.emob.model.request.promotion;

import com.example.emob.constant.PromotionType;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PromotionValueRequest {
    float value;
    float minPrice;
    LocalDateTime startDate;
    LocalDateTime endDate;
    PromotionType type;
}
