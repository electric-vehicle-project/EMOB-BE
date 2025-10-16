/* EMOB-2025 */
package com.example.emob.model.request.delivery;

import com.fasterxml.jackson.annotation.JsonInclude;
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
@JsonInclude(JsonInclude.Include.NON_NULL)
public class DeliveryRequest {
  UUID contractId;
  Set<DeliveryItemRequest> deliveryItems;
  LocalDateTime deliveryDate;
}
