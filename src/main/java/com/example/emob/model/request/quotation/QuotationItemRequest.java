/* EMOB-2025 */
package com.example.emob.model.request.quotation;

import com.example.emob.constant.VehicleStatus;
import java.util.UUID;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class QuotationItemRequest {
  UUID vehicleId;
  UUID promotionId;
  VehicleStatus vehicleStatus;
  String color;
//  int quantity;
}
