package com.example.emob.entity;

import com.example.emob.constant.VehicleType;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.UuidGenerator;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Entity
@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ElectricVehicle {
    @Id
    @UuidGenerator
    UUID id;

    String brand;
    String model;
    float importPrice;
    float retailPrice;
    Float batteryKwh;
    Integer rangeKm;
    Float chargeTimeHr;
    Float powerKw;

    @ElementCollection
    @CollectionTable(
            name = "electric_vehicle_images",
            joinColumns = @JoinColumn(name = "vehicle_id")
    )
    @Column(name = "image_url")
    List<String> images;

    Float weightKg;
    Float topSpeedKmh;

    @Enumerated(EnumType.STRING)
    VehicleType type;
    LocalDate createdAt;
}
