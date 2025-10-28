/* EMOB-2025 */
package com.example.emob.model.response.saleContract;

import com.example.emob.constant.VehicleStatus;
import com.fasterxml.jackson.annotation.JsonInclude;
import java.math.BigDecimal;
import java.util.Set;
import java.util.UUID;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ContractItemResponse {
  UUID id;
  UUID vehicleId;
  Set<UUID> vehicleUnitIds;
  UUID promotionId;
  VehicleStatus vehicleStatus;
  String color;
  int quantity;
  BigDecimal unitPrice;
  BigDecimal discountPrice;
  BigDecimal totalPrice;
}
