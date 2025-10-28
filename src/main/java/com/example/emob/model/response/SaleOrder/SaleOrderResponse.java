/* EMOB-2025 */
package com.example.emob.model.response.SaleOrder;

import com.example.emob.constant.OrderStatus;
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
public class SaleOrderResponse {
  UUID id;
  Set<SaleOrderItemResponse> items;
  UUID customerId;
  UUID dealerId;
  UUID saleContractId;
  UUID accountId;
  BigDecimal totalPrice;
  BigDecimal vatAmount;
  int totalQuantity;
  OrderStatus status;
  LocalDateTime createdAt;
}
