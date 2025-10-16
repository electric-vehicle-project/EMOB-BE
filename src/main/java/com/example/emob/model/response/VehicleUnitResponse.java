/* EMOB-2025 */
package com.example.emob.model.response;

import com.example.emob.constant.VehicleStatus;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class VehicleUnitResponse {
  UUID vehicleUnitId;
  String vinNumber;
  double price;
  LocalDateTime purchaseDate;
  LocalDate warrantyStart;
  LocalDate warrantyEnd;
  LocalDate productionYear;
  VehicleStatus status;
  String color;
}
