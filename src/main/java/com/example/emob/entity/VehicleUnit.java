/* EMOB-2025 */
package com.example.emob.entity;

import com.example.emob.constant.VehicleStatus;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;
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
public class VehicleUnit {
  @Id @UuidGenerator UUID id;
  String color;

  @Column(name = "vin_number", unique = true, nullable = false, length = 17)
  String vinNumber;

  BigDecimal price;
  LocalDateTime purchaseDate;
  LocalDate warrantyStart;
  LocalDate warrantyEnd;
  LocalDate productionYear;

  @Enumerated(EnumType.STRING)
  VehicleStatus status;

  @ManyToOne
  @JoinColumn(name = "vehicle_id")
  @JsonIgnore
  ElectricVehicle vehicle;

  @OneToMany(mappedBy = "vehicleUnit", cascade = CascadeType.ALL, orphanRemoval = true)
  @JsonIgnore
  Set<TestDrive> testDrive;

  @ManyToOne
  @JoinColumn(name = "inventory_id")
  @JsonIgnore
  Inventory inventory;

  @ManyToMany(mappedBy = "vehicleUnits")
  Set<Delivery> deliveries = new HashSet<>();

  @ManyToOne
  @JoinColumn(name = "saleOrderItem_id")
  @JsonIgnore
  SaleOrderItem saleOrderItem;

  @ManyToOne
  @JoinColumn(name = "saleContractItem_id")
  @JsonIgnore
  SaleContractItem SaleContractItem;
}
