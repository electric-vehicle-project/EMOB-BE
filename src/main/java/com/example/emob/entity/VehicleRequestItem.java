/* EMOB-2025 */
package com.example.emob.entity;

import com.example.emob.constant.VehicleStatus;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import java.math.BigDecimal;
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
public class VehicleRequestItem {
  @Id @UuidGenerator UUID id;

  BigDecimal unitPrice;
  BigDecimal totalPrice;
  boolean isDeleted = false;

  @Enumerated(EnumType.STRING)
  VehicleStatus vehicleStatus;

  int quantity;
  String color;

  @ManyToOne
  @JoinColumn(name = "vehicle_id")
  @JsonIgnore
  ElectricVehicle vehicle;

  @ManyToOne
  @JoinColumn(name = "vehicleRequest_id")
  @JsonIgnore
  VehicleRequest vehicleRequest;
}
