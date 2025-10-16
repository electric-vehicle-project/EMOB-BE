/* EMOB-2025 */
package com.example.emob.model.request.promotion;

import com.example.emob.constant.PromotionType;
import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import java.time.LocalDateTime;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PromotionValueRequest {
  @Positive(message = "Value must be greater than 0")
  float value;

  @PositiveOrZero(message = "Minimum price must be 0 or greater")
  float minPrice;

  @NotNull(message = "Start date cannot be null")
  LocalDateTime startDate;

  @NotNull(message = "End date cannot be null")
  LocalDateTime endDate;

  @NotNull(message = "Promotion type cannot be null")
  PromotionType type;
}
