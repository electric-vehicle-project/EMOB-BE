/* EMOB-2025 */
package com.example.emob.model.response;

import com.example.emob.constant.InstallmentStatus;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class InstallmentResponse {
  UUID id;
  LocalDateTime downDate; // ngày đặt cọc
  BigDecimal deposit; // tiền đặt cọc
  BigDecimal totalAmount; // tổng tiền cần trả góp
  BigDecimal monthlyAmount; // số tiền phải trả mỗi tháng
  float interestRate; // lãi suất
  int termMonths; // thời hạn trả góp theo tháng
  LocalDate nextDueDate; // ngày thanh toán tiếp theo
  InstallmentStatus status;
}
