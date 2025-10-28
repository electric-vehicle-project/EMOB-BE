/* EMOB-2025 */
package com.example.emob.model.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import java.math.BigDecimal;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class DealerPointRuleResponse {
  String membershipLevel;
  String dealerId;
  int minPoints;
  BigDecimal price;
}
