/* EMOB-2025 */
package com.example.emob.model.request;

import com.example.emob.constant.Gender;
import com.example.emob.constant.VehicleStatus;
import com.example.emob.validator.EnumValidator;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class VehiclePriceRuleRequest {

  @NotNull(message = "FIELD_REQUIRED")
  @EnumValidator(enumClass = VehicleStatus.class)
  VehicleStatus vehicleStatus; // REAL, TEST_DRIVE, SPECIAL...
  @NotNull(message = "FIELD_REQUIRED")
  Double multiplier; // Hệ số giá
  String note;
}
