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
public class SaleContractItem {
  @Id @GeneratedValue UUID id;

  BigDecimal unitPrice;
  BigDecimal totalPrice;
  BigDecimal discountPrice;

  @Enumerated(EnumType.STRING)
  VehicleStatus vehicleStatus;

  int quantity;
  String color;

  @OneToOne
  @JoinColumn(name = "promotion_id")
  Promotion promotion;

  @OneToMany(mappedBy = "SaleContractItem", cascade = CascadeType.ALL, orphanRemoval = true)
  Set<VehicleUnit> vehicleUnits = new HashSet<>();

  @ManyToOne
  @JoinColumn(name = "vehicle_id")
  @JsonIgnore
  ElectricVehicle vehicle;

  @ManyToOne
  @JoinColumn(name = "saleContract_id")
  @JsonIgnore
  SaleContract saleContract;
}
