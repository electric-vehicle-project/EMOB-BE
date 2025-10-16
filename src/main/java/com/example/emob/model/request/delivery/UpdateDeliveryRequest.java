/* EMOB-2025 */
package com.example.emob.model.request.delivery;

import java.time.LocalDateTime;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UpdateDeliveryRequest {
  LocalDateTime deliveryDate;
}
