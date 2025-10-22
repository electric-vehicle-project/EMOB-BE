package com.example.emob.model.response.SaleOrder;
import com.example.emob.constant.OrderStatus;
import com.example.emob.constant.QuotationStatus;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Set;
import java.util.UUID;
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
    UUID accountId;
    BigDecimal totalPrice;
    int totalQuantity;
    int validUntil;
    OrderStatus status;
    LocalDateTime createdAt;





}
