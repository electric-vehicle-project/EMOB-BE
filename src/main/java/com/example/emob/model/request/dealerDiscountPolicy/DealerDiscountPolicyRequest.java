package com.example.emob.model.request.dealerDiscountPolicy;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

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
