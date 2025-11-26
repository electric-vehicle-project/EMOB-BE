/* EMOB-2025 */
package com.example.emob.entity;

import com.example.emob.constant.QuotationStatus;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.UuidGenerator;

@Entity
@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Quotation {
  @Id @UuidGenerator UUID id;
  BigDecimal totalPrice;
  BigDecimal vatAmount;
//  int totalQuantity;
  int validUntil;
  boolean isDeleted = false;

  @Enumerated(EnumType.STRING)
  QuotationStatus status;

  LocalDateTime createdAt;
  LocalDateTime updatedAt;

  @OneToMany(mappedBy = "quotation", cascade = CascadeType.ALL, orphanRemoval = true)
  Set<QuotationItem> quotationItems = new HashSet<>();

  @ManyToOne
  @JoinColumn(name = "dealer_id")
  @JsonIgnore
  Dealer dealer;

  @ManyToOne
  @JoinColumn(name = "account_id")
  @JsonIgnore
  Account account;

  @ManyToOne
  @JoinColumn(name = "customer_id")
  @JsonIgnore
  Customer customer;

  @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
  @JoinColumn(name = "sale_order_id")
  SaleOrder saleOrder;
}
