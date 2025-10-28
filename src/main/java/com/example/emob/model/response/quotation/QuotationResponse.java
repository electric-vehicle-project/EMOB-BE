/* EMOB-2025 */
package com.example.emob.model.response.quotation;

import com.example.emob.constant.QuotationStatus;
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
public class QuotationResponse {
  UUID id;
  Set<QuotationItemResponse> items;
  UUID customerId;
  UUID dealerId;
  UUID accountId;
  UUID saleOrderId;
  BigDecimal totalPrice;
  BigDecimal vatAmount;
  int totalQuantity;
  int validUntil;
  QuotationStatus status;
  LocalDateTime createdAt;
}
