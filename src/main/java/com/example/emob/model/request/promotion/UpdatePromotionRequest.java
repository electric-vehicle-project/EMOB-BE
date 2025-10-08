/* EMOB-2025 */
package com.example.emob.model.request.promotion;

import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class UpdatePromotionRequest {
    @NotNull String name;
    String description;
    float value;
    float minValue;
    LocalDateTime startDate;
    LocalDateTime endDate;
}
