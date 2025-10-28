/* EMOB-2025 */
package com.example.emob.model.response.saleContract;

import com.example.emob.constant.ContractStatus;
import com.fasterxml.jackson.annotation.JsonInclude;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Set;
import java.util.UUID;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ContractResponse {
  UUID orderId;
  UUID contractId;
  UUID deliveryId;
  Set<ContractItemResponse> items;
  String contractNumber;
  BigDecimal totalPrice;
  BigDecimal vatAmount;
  int totalQuantity;
  LocalDateTime createAt;
  ContractStatus status;
  LocalDateTime signDate;
}
