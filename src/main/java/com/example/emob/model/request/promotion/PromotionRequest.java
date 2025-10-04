package com.example.emob.model.request.promotion;

import com.example.emob.constant.PromotionType;
import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PromotionRequest {
    UUID evmStaffId;
    Set<UUID> dealerId;
    @NotNull
    String name;
    String description;
    PromotionType type;
    float value;
    float minValue;
    LocalDateTime startDate;
    LocalDateTime endDate;
}


