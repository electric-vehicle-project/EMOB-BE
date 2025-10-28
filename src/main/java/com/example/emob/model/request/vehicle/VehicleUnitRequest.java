/* EMOB-2025 */
package com.example.emob.model.request.vehicle;

import com.example.emob.constant.VehicleStatus;
import jakarta.validation.constraints.*;
import java.time.LocalDate;
import java.util.UUID;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class VehicleUnitRequest {

  @NotNull(message = "FIELD_REQUIRED")
  UUID vehicleId;

  @Min(value = 1, message = "INVALID_MIN_0")
  int quantity;

  @NotBlank(message = "FIELD_REQUIRED")
  String color;

  @PastOrPresent(message = "INVALID_DATE")
  LocalDate productionYear;

  @NotNull(message = "FIELD_REQUIRED")
  VehicleStatus status;
}
