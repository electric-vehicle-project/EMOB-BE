/* EMOB-2025 */
package com.example.emob.model.response;

import java.util.List;
import java.util.UUID;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class PromotionHistoryDealerResponse {
  UUID dealerId;
  String dealerName;
  String country;
  List<PromotionResponse> promotions;
}
