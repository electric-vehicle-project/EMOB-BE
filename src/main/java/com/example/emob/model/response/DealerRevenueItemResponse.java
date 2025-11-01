package com.example.emob.model.response;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class DealerRevenueItemResponse {
    UUID dealerId;
    BigDecimal totalRevenue;   // tổng doanh thu
    Long totalContracts;       // đổi sang Long (wrapper)
    Long totalVehiclesSold;    // đổi sang Long (wrapper)
    public DealerRevenueItemResponse(UUID dealerId, BigDecimal totalRevenue, Long totalContracts, Long totalVehiclesSold) {
        this.dealerId = dealerId;
        this.totalRevenue = totalRevenue;
        this.totalContracts = totalContracts;
        this.totalVehiclesSold = totalVehiclesSold;
    }}
