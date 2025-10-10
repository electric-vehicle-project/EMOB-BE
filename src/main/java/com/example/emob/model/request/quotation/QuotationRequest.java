/* EMOB-2025 */
package com.example.emob.model.request.quotation;

import java.util.List;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class QuotationRequest {
  List<QuotationItemRequest> requests;
}
