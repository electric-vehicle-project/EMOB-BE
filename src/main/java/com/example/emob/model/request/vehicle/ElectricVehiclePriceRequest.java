/* EMOB-2025 */
package com.example.emob.model.request.vehicle;

import java.math.BigDecimal;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ElectricVehiclePriceRequest {
  @NotNull(message = "FIELD_REQUIRED")
  @DecimalMin(value = "0.0", inclusive = true, message = "INVALID_MIN_PRICE")
  BigDecimal importPrice;
  @NotNull(message = "FIELD_REQUIRED")
  @DecimalMin(value = "0.0", inclusive = true, message = "INVALID_MIN_PRICE")
  BigDecimal retailPrice;
}
