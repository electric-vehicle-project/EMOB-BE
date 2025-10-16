package com.example.emob.model.response;

import com.example.emob.constant.DeliveryStatus;
import com.example.emob.constant.OrderStatus;
import com.example.emob.constant.PaymentStatus;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class OrderHistoryDealerResponse {
    UUID orderId;
    LocalDateTime orderDate;
    OrderStatus orderStatus;
    PaymentStatus paymentStatus;
    float price;
    String contractNumber;
    LocalDateTime signDate;
    DeliveryStatus deliveryStatus;
}
