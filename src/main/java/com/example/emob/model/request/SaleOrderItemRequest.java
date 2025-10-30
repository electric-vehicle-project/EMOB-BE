/* EMOB-2025 */
package com.example.emob.model.request;

import java.util.UUID;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class SaleOrderItemRequest {
  UUID itemsId;
  UUID promotionId;
  int quantity;
}
