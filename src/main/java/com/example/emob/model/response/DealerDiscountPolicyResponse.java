package com.example.emob.model.response;

import com.example.emob.constant.DiscountPolicyStatus;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class DealerDiscountPolicyResponse {
    UUID id;
    double customMultiplier;
    LocalDateTime createAt;
    BigDecimal finalPrice;
    LocalDateTime updateAt;
    LocalDate effectiveDate;
    LocalDate expiryDate;
    UUID dealerId;
    UUID vehicleId;
    DiscountPolicyStatus status;
}
