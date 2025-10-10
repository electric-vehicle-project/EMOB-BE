/* EMOB-2025 */
package com.example.emob.entity;

import com.example.emob.constant.VehicleType;
import jakarta.persistence.*;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
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
public class ElectricVehicle {
  @Id @UuidGenerator UUID id;

  String brand;
  String model;
  float importPrice;
  float retailPrice;
  Float batteryKwh;
  Integer rangeKm;
  Float chargeTimeHr;
  Float powerKw;
  boolean isDeleted = false;

  @ElementCollection
  @CollectionTable(name = "electric_vehicle_images", joinColumns = @JoinColumn(name = "vehicle_id"))
  @Column(name = "image_url")
  List<String> images;

  Float weightKg;
  Float topSpeedKmh;

  @Enumerated(EnumType.STRING)
  VehicleType type;

  LocalDate createdAt;

  @ManyToMany(mappedBy = "vehicles")
  Set<Promotion> promotions = new HashSet<>();

  @OneToMany(mappedBy = "vehicle", cascade = CascadeType.ALL, orphanRemoval = true)
  Set<VehicleUnit> vehicleUnits = new HashSet<>();

  @OneToMany(mappedBy = "vehicle", cascade = CascadeType.ALL, orphanRemoval = true)
  Set<QuotationItem> quotationItems = new HashSet<>();
}
