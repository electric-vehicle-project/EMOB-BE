package com.example.emob.model.request.installment;

import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.constraints.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class InstallmentRequest {
    @NotNull(message = "Order ID must not be null")
    UUID orderId;
    @NotNull(message = "Deposit amount must not be null")
    @DecimalMin(value = "0.0", inclusive = false, message = "Deposit amount must be greater than 0")
    BigDecimal deposit; // tiền đạt cọc
    @NotNull(message = "Down payment date must not be null")
    LocalDateTime downPayment; // ngày trả góp
    @NotNull(message = "Total amount must not be null")
    @DecimalMin(value = "0.0", inclusive = false, message = "Total amount must be greater than 0")
    BigDecimal totalAmount; // tổng tiền cần trả góp
    @Min(value = 1, message = "Term must be at least 1 month")
    @Max(value = 15, message = "Term must not exceed 15 months")
    int termMonths;
    @PositiveOrZero(message = "Interest rate cannot be negative")
    float interestRate; // lãi suất
}
