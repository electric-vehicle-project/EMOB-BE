/* EMOB-2025 */
package com.example.emob.model.request.vehicle;

import com.example.emob.constant.VehicleType;
import java.util.List;

import jakarta.validation.constraints.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ElectricVehicleRequest {
  @NotBlank(message = "FIELD_REQUIRED")
  @Size(max = 100, message = "INVALID_SIZE_100")
  String brand;

  @NotBlank(message = "FIELD_REQUIRED")
  @Size(max = 100, message = "INVALID_SIZE_100")
  String model;

  @NotNull(message = "FIELD_REQUIRED")
  @DecimalMin(value = "0.1", inclusive = false, message = "INVALID_MIN_PRICE")
  Float batteryKwh;

  @NotNull(message = "FIELD_REQUIRED")
  @Min(value = 1, message = "INVALID_MIN_0")
  Integer rangeKm;

  @NotNull(message = "FIELD_REQUIRED")
  @DecimalMin(value = "0.1", inclusive = false, message = "INVALID_MIN_0")
  Float chargeTimeHr;

  @NotNull(message = "FIELD_REQUIRED")
  @DecimalMin(value = "0.1", inclusive = false, message = "INVALID_MIN_0")
  Float powerKw;

  @NotEmpty(message = "FIELD_REQUIRED")
  List<@NotBlank(message = "FIELD_REQUIRED") String> images;

  @NotNull(message = "FIELD_REQUIRED")
  @DecimalMin(value = "0.1", inclusive = false, message = "INVALID_MIN_0")
  Float weightKg;

  @NotNull(message = "FIELD_REQUIRED")
  @DecimalMin(value = "0.1", inclusive = false, message = "INVALID_MIN_0")
  Float topSpeedKmh;


  @NotNull(message = "FIELD_REQUIRED")
  VehicleType type;
}
