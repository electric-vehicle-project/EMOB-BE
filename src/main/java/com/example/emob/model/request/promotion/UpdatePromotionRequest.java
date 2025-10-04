package com.example.emob.model.request.promotion;

import com.example.emob.constant.PromotionScope;
import com.example.emob.constant.PromotionStatus;
import com.example.emob.entity.Dealer;
import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;
import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class UpdatePromotionRequest {
    @NotNull
    String name;
    String description;
    float value;
    float minValue;
    LocalDateTime startDate;
    LocalDateTime endDate;
    PromotionStatus status;
}
