package com.example.emob.model.response;

import java.math.BigDecimal;

public interface MonthlyRevenueResponse {
    Integer getMonth();
    BigDecimal getTotalRevenue();
    Long getTotalContracts();
    Long getTotalVehiclesPurchased();
}
