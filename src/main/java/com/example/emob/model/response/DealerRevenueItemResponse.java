/* EMOB-2025 */
package com.example.emob.model.response;

import java.math.BigDecimal;

// dùng Spring Data Projection
// map tự động
public interface DealerRevenueItemResponse {
  String getDealerId();
  String getRegion();
  String getCountry();

  BigDecimal getTotalRevenue();

  Long getTotalContracts();

  Long getTotalVehiclesSold();

  Integer getMonth();

  Integer getYear();
}
