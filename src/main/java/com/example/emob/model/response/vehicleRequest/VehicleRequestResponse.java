/* EMOB-2025 */
package com.example.emob.model.response.vehicleRequest;

import com.example.emob.constant.VehicleRequestStatus;
import com.fasterxml.jackson.annotation.JsonInclude;
import java.math.BigDecimal;
import java.time.LocalDateTime;
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
public class VehicleRequestResponse {
  UUID id;
  Set<VehicleRequestItemResponse> items;
  UUID dealerId;
  UUID saleOrderId;
  BigDecimal totalPrice;
  int totalQuantity;
  VehicleRequestStatus status;
  LocalDateTime createdAt;
  LocalDateTime updatedAt;
}
