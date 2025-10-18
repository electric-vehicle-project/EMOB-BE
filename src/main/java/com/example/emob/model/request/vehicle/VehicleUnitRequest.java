/* EMOB-2025 */
package com.example.emob.model.request.vehicle;

import com.example.emob.constant.VehicleStatus;
import jakarta.validation.constraints.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

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
  LocalDateTime purchaseDate;

  @NotNull(message = "FIELD_REQUIRED")
  @FutureOrPresent(message = "INVALID_DATE")
  LocalDate warrantyStart;

  @NotNull(message = "FIELD_REQUIRED")
  @Future(message = "INVALID_DATE")
  LocalDate warrantyEnd;

  @PastOrPresent(message = "INVALID_DATE")
  LocalDate productionYear;

  @NotNull(message = "FIELD_REQUIRED")
  VehicleStatus status;
}
