/* EMOB-2025 */
package com.example.emob.model.request.vehicle;

import com.example.emob.constant.VehicleType;
import java.util.List;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ElectricVehicleRequest {
  String brand;
  String model;
  Float batteryKwh;
  Integer rangeKm;
  Float chargeTimeHr;
  Float powerKw;
  List<String> images;
  Float weightKg;
  Float topSpeedKmh;
  VehicleType type;
}
