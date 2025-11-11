package com.example.emob.model.response;

import java.math.BigDecimal;

public interface MonthlyDealerRevenueResponse {
    Integer getMonth();
    BigDecimal getTotalRevenue();
    Long getTotalContracts();
    Long getTotalVehiclesSold();
}
