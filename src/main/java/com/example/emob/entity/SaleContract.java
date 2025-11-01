/* EMOB-2025 */
package com.example.emob.entity;

import com.example.emob.constant.ContractStatus;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;
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
@Table(name = "sale_contract")
public class SaleContract {
  @Id
  @GeneratedValue
  @Column(columnDefinition = "BINARY(16)", unique = true)
  UUID id;

  @Column(unique = true)
  String contractNumber; // mã số hợp đồng

  LocalDate signDate;
  BigDecimal totalPrice;
  BigDecimal vatAmount;
  int totalQuantity;

  @Enumerated(EnumType.STRING)
  ContractStatus status;

  LocalDateTime createdAt;

  @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
  @JoinColumn(name = "sale_order")
  SaleOrder saleOrder;

  @ManyToOne
  @JoinColumn(name = "created_by", referencedColumnName = "id")
  Account account;

  @OneToOne(mappedBy = "saleContract", cascade = CascadeType.ALL)
  Delivery delivery;

  @OneToMany(mappedBy = "saleContract", cascade = CascadeType.ALL, orphanRemoval = true)
  Set<SaleContractItem> saleContractItems = new HashSet<>();
}
