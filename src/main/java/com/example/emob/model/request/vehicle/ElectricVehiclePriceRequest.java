/* EMOB-2025 */
package com.example.emob.model.request.vehicle;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ElectricVehiclePriceRequest {
  float importPrice;
  float retailPrice;
}
