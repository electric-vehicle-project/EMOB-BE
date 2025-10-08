/* EMOB-2025 */
package com.example.emob.model.request.promotion;

import com.example.emob.constant.MemberShipLevel;
import com.example.emob.constant.PromotionType;
import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.Set;
import java.util.UUID;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PromotionRequest {
    UUID staffId;
    Set<UUID> dealerId;
    Set<UUID> electricVehiclesId;
    @NotNull String name;
    String description;
    PromotionType type;
    float value;
    float minValue;
    LocalDateTime startDate;
    LocalDateTime endDate;
    MemberShipLevel memberShipLevel;
}
