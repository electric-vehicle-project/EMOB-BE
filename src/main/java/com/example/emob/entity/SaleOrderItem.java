/* EMOB-2025 */
package com.example.emob.entity;

import com.example.emob.constant.VehicleStatus;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import java.math.BigDecimal;
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
public class SaleOrderItem {
  @Id @GeneratedValue UUID id;

  BigDecimal unitPrice;
  BigDecimal totalPrice;
  BigDecimal discountPrice;
  boolean isDeleted = false;

  @Enumerated(EnumType.STRING)
  VehicleStatus vehicleStatus;

  int quantity;
  String color;

  @ManyToOne
  @JoinColumn(name = "promotion_id")
  @JsonIgnore
  Promotion promotion;

  @OneToMany(mappedBy = "saleOrderItem", cascade = CascadeType.ALL, orphanRemoval = true)
  Set<VehicleUnit> vehicleUnits = new HashSet<>();

  @ManyToOne
  @JoinColumn(name = "saleOrder_id")
  @JsonIgnore
  SaleOrder saleOrder;

  @ManyToOne
  @JoinColumn(name = "vehicle_id")
  @JsonIgnore
  ElectricVehicle vehicle;
}
