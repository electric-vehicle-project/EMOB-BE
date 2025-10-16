/* EMOB-2025 */
package com.example.emob.model.request.delivery;

import java.util.UUID;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UpdateDeliveryItemRequest {
  UUID vehicleId;
  String remarks;
}
