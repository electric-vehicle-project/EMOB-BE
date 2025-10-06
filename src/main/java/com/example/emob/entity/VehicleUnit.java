package com.example.emob.entity;

import com.example.emob.constant.VehicleStatus;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.UuidGenerator;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class VehicleUnit {
    @Id
    @UuidGenerator
    UUID id;
    String color;
    @Column(name = "vin_number", unique = true, nullable = false, length = 17)
    String vinNumber;

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

}
