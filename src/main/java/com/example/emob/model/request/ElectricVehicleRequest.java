package com.example.emob.model.request;

import com.example.emob.constant.VehicleType;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ElectricVehicleRequest {
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
}
