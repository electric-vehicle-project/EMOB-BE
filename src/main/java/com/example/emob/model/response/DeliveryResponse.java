/* EMOB-2025 */
package com.example.emob.model.response;

import com.example.emob.constant.DeliveryStatus;
import java.time.LocalDateTime;
import java.util.Set;
import java.util.UUID;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class DeliveryResponse {
  UUID id;
  Set<DeliveryItemResponse> deliveryItems;
  LocalDateTime deliveryDate;
  int quantity;
  DeliveryStatus status;
}
