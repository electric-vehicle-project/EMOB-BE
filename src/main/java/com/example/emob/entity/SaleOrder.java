/* EMOB-2025 */
package com.example.emob.entity;

import com.example.emob.constant.OrderStatus;
import com.example.emob.constant.PaymentStatus;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Entity
@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class SaleOrder {
  @Id @GeneratedValue UUID id;

  BigDecimal totalPrice;
  int totalQuantity;
  BigDecimal vatAmount;

  LocalDateTime createdAt;

  @Enumerated(EnumType.STRING)
  PaymentStatus paymentStatus;

  @Enumerated(EnumType.STRING)
  OrderStatus status;

  @ManyToOne
  @JoinColumn(name = "account_id", referencedColumnName = "id")
  Account account;

  @OneToMany(mappedBy = "saleOrder", cascade = CascadeType.ALL, orphanRemoval = true)
  Set<SaleOrderItem> saleOrderItems = new HashSet<>();

  @OneToOne(mappedBy = "saleOrder", cascade = CascadeType.ALL)
  @JsonIgnore
  SaleContract contract;

  @OneToOne(mappedBy = "saleOrder", cascade = CascadeType.ALL)
  @JsonIgnore
  InstallmentPlan installmentPlan;

  @OneToOne(mappedBy = "saleOrder", cascade = CascadeType.ALL)
  @JsonIgnore
  Quotation quotation;

  @OneToOne(mappedBy = "saleOrder", cascade = CascadeType.ALL)
  @JsonIgnore
  VehicleRequest vehicleRequest;
}
