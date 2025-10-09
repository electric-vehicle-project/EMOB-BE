/* EMOB-2025 */
package com.example.emob.model.request.quotation;

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
  String color;
  int quantity;
}
