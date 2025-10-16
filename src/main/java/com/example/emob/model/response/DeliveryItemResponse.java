/* EMOB-2025 */
package com.example.emob.model.response;

import com.example.emob.constant.DeliveryItemStatus;
import java.util.UUID;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class DeliveryItemResponse {
  UUID id;
  UUID vehicleId;
  DeliveryItemStatus status;
  String remarks;
}
