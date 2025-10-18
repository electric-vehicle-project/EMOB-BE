/* EMOB-2025 */
package com.example.emob.model.request.dealerDiscountPolicy;

import jakarta.validation.constraints.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class DealerDiscountPolicyBulkRequest {
  @NotEmpty(message = "FIELD_REQUIRED") // vì là List
  private List<UUID> dealerIds; // danh sách ID đại lý

  @NotEmpty(message = "FIELD_REQUIRED") // vì là List
  private List<UUID> vehicleModelIds; // danh sách ID loại xe

  @NotNull(message = "FIELD_REQUIRED")
  @DecimalMin(value = "0.0", inclusive = false, message = "INVALID_MIN_0")
  private Double customMultiplier; // hệ số chiết khấu áp dụng (phải > 0)

  @DecimalMin(value = "0.0", inclusive = false, message = "INVALID_MIN_0")
  private BigDecimal finalPrice; // nếu muốn chốt giá cố định (tùy chọn)

  @NotNull(message = "FIELD_REQUIRED")
  @FutureOrPresent(message = "INVALID_DATE")
  private LocalDate effectiveDate;

  @NotNull(message = "FIELD_REQUIRED")
  @Future(message = "INVALID_DATE")
  private LocalDate expiredDate;
}
