/* EMOB-2025 */
package com.example.emob.model.request;

import com.example.emob.constant.VehicleStatus;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class VehiclePriceRuleRequest {
  VehicleStatus vehicleStatus; // REAL, TEST_DRIVE, SPECIAL...

  Double multiplier; // Hệ số giá
  String note;
}
