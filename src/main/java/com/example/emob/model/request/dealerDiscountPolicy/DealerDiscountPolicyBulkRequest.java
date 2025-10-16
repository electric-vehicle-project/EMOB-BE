/* EMOB-2025 */
package com.example.emob.model.request.dealerDiscountPolicy;

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
  List<UUID> dealerIds; // danh sách ID đại lý
  List<UUID> vehicleModelIds; // danh sách ID loại xe
  Double customMultiplier; // hệ số chiết khấu áp dụng
  BigDecimal finalPrice; // nếu muốn chốt giá cố định
  LocalDate effectiveDate;
  LocalDate expiredDate;
}
