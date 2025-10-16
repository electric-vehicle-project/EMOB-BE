/* EMOB-2025 */
package com.example.emob.model.request.vehicle;

import java.math.BigDecimal;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ElectricVehiclePriceRequest {
  BigDecimal importPrice;
  BigDecimal retailPrice;
}
