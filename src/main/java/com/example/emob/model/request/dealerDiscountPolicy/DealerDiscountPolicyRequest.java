/* EMOB-2025 */
package com.example.emob.model.request.dealerDiscountPolicy;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotNull;
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
  @NotNull(message = "FIELD_REQUIRED")
  @DecimalMin(value = "0.0", inclusive = false, message = "INVALID_MIN_0")
  private Double customMultiplier; // hệ số chiết khấu (phải > 0)

  @DecimalMin(value = "0.0", inclusive = false, message = "INVALID_MIN_0")
  private BigDecimal finalPrice; // nếu có, phải > 0

  @NotNull(message = "FIELD_REQUIRED")
  @FutureOrPresent(message = "INVALID_DATE")
  private LocalDate effectiveDate; // ngày hiệu lực

  @NotNull(message = "FIELD_REQUIRED")
  @Future(message = "INVALID_DATE")
  private LocalDate expiryDate; // ngày hết hạn

  @NotNull(message = "FIELD_REQUIRED")
  private UUID dealerId; // đại lý áp dụng

  @NotNull(message = "FIELD_REQUIRED")
  private UUID vehicleId; // loại xe áp dụng
}
