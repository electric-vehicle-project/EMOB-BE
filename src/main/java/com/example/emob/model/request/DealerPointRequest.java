/* EMOB-2025 */
package com.example.emob.model.request;

import com.example.emob.constant.MemberShipLevel;
import jakarta.validation.constraints.*;
import java.math.BigDecimal;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class DealerPointRequest {
  @NotNull(message = "Membership level cannot be null")
  MemberShipLevel membershipLevel;

  @NotBlank(message = "Dealer ID cannot be blank")
  String dealerId;

  @Min(value = 0, message = "Minimum points must be greater than or equal to 0")
  int minPoints;

  @NotNull(message = "Discount price cannot be null")
  BigDecimal price;
}
