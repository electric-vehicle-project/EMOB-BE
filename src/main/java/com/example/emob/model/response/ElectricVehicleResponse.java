/* EMOB-2025 */
package com.example.emob.model.response;

import com.example.emob.constant.VehicleType;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ElectricVehicleResponse {
    UUID id;
    String brand;
    String model;
    float importPrice;
    float retailPrice;
    Float batteryKwh;
    Integer rangeKm;
    Float chargeTimeHr;
    Float powerKw;
    List<String> images;
    Float weightKg;
    Float topSpeedKmh;
    VehicleType type;
    LocalDate createdAt;
}
