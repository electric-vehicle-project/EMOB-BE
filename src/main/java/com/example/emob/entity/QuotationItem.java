/* EMOB-2025 */
package com.example.emob.entity;

import com.example.emob.constant.VehicleStatus;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import java.math.BigDecimal;
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
public class QuotationItem {
  @Id @GeneratedValue UUID id;

  BigDecimal unitPrice;
  BigDecimal totalPrice;
  BigDecimal discountPrice;
  boolean isDeleted = false;

  @Enumerated(EnumType.STRING)
  VehicleStatus vehicleStatus;

  int quantity;
  String color;

  @ManyToOne(cascade = CascadeType.ALL)
  @JoinColumn(name = "promotion_id")
  Promotion promotion;

  @ManyToOne(cascade = CascadeType.ALL)
  @JoinColumn(name = "quotation_id")
  @JsonIgnore
  Quotation quotation;

  @ManyToOne
  @JoinColumn(name = "vehicle_id")
  @JsonIgnore
  ElectricVehicle vehicle;
}
