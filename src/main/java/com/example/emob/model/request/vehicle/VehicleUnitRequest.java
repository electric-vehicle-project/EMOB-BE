/* EMOB-2025 */
package com.example.emob.model.request.vehicle;

import com.example.emob.constant.VehicleStatus;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;
import lombok.*;
import lombok.experimental.FieldDefaults;

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
    VehicleStatus status;
}
