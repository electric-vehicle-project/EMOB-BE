/* EMOB-2025 */
package com.example.emob.model.request;

import com.example.emob.constant.MemberShipLevel;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class DealerPointRuleRequest {
  @NotNull(message = "MembershipLevel is required")
  MemberShipLevel level;

  @NotNull(message = "DealerId is required")
  String dealerId;

  int minPoints;
  BigDecimal price;
}
