package com.example.emob.model.response;

import java.math.BigDecimal;

public interface CustomerRevenueItemResponse {
    String getCustomerId();
    BigDecimal getTotalRevenue();
    Long getTotalContracts();
    Long getTotalVehiclesPurchased();
    Integer getMonth();
    Integer getYear();
}
