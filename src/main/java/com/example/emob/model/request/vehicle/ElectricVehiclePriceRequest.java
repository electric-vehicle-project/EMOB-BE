/* EMOB-2025 */
package com.example.emob.model.request.vehicle;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
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
