/* EMOB-2025 */
package com.example.emob.entity;

import com.example.emob.constant.DeliveryStatus;
import jakarta.persistence.*;
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
public class Delivery {
  @Id @UuidGenerator UUID id;

  LocalDateTime deliveryDate;

  int quantity;

  boolean isDeleted;

  @Enumerated(EnumType.STRING)
  DeliveryStatus status;

  LocalDateTime createAt;

  LocalDateTime completedAt;

  @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
  @JoinColumn(name = "saleContract_id")
  SaleContract saleContract;

  @ManyToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
  @JoinTable(
      name = "delivery_vehicle_unit",
      joinColumns = @JoinColumn(name = "delivery_id"),
      inverseJoinColumns = @JoinColumn(name = "vehicle_unit_id"))
  private Set<VehicleUnit> vehicleUnits = new HashSet<>();
}
