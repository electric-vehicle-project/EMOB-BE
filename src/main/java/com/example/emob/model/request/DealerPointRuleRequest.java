package com.example.emob.model.request;

import com.example.emob.constant.MemberShipLevel;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class DealerPointRuleRequest {
    @NotNull(message = "MembershipLevel is required")
    MemberShipLevel level;
    @NotNull(message = "DealerId is required")
    String dealerId;
    int minPoints;
    BigDecimal price;
}
