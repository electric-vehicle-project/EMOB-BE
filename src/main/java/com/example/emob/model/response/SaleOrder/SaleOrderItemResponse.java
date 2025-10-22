/* EMOB-2025 */
package com.example.emob.model.response.SaleOrder;

import com.example.emob.constant.VehicleStatus;
import com.fasterxml.jackson.annotation.JsonInclude;
import java.math.BigDecimal;
import java.util.UUID;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class SaleOrderItemResponse {
  UUID id;
  UUID vehicleId;
  UUID promotionId;
  VehicleStatus vehicleStatus;
  String color;
  int quantity;
  BigDecimal unitPrice;
  BigDecimal discountPrice;
  BigDecimal totalPrice;
}
