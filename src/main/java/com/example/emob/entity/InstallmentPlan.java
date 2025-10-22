/* EMOB-2025 */
package com.example.emob.entity;

import com.example.emob.constant.InstallmentStatus;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Entity
@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class InstallmentPlan {
  @Id @GeneratedValue UUID id;
  LocalDateTime downDate; // ngày đặt cọc
  BigDecimal deposit; // tiền đặt cọc
  BigDecimal totalAmount; // tổng tiền cần trả góp
  BigDecimal monthlyAmount; // số tiền phải trả mỗi tháng
  float interestRate; // lãi suất
  int termMonths; // thời hạn trả góp theo tháng
  LocalDate nextDueDate; // ngày thanh toán tiếp theo

  @Enumerated(EnumType.STRING)
  InstallmentStatus status;

  LocalDateTime updateAt;

  LocalDate lastReminderDate; // lần nhắc cuối cùng trong ngày
  Integer reminderCount; // số lần nhắc nhỏ khi OVERDUE

  @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
  @JoinColumn(name = "sale_order_id")
  SaleOrder saleOrder;
}
