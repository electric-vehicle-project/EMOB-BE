package com.example.emob.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.UuidGenerator;

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
}
