package com.example.emob.model.request;

import jakarta.persistence.Column;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class VehicleUnitRequest {
    UUID vehicleId;
    int quantity;
    String color;
    LocalDateTime purchaseDate;
    LocalDate warrantyStart;
    LocalDate warrantyEnd;
    LocalDate productionYear;
}
