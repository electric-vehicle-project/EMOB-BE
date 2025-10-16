/* EMOB-2025 */
package com.example.emob.model.request.dealerDiscountPolicy;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class DealerDiscountPolicyRequest {
  Double customMultiplier;
  BigDecimal finalPrice;
  LocalDate effectiveDate;
  LocalDate expiryDate;
  UUID dealerId;
  UUID vehicleId;
}
