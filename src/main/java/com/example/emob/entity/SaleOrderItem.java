package com.example.emob.entity;

import com.example.emob.constant.VehicleStatus;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;
import java.util.UUID;

@Entity
@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class SaleOrderItem {
    @Id
    @GeneratedValue
    UUID id;

    BigDecimal unitPrice;
    BigDecimal totalPrice;
    BigDecimal discountPrice;
    boolean isDeleted = false;
    @Enumerated(EnumType.STRING)
    VehicleStatus vehicleStatus;

    int quantity;
    String color;

    @OneToOne
    @JoinColumn(name = "promotion_id")
    Promotion promotion;

    @ManyToOne
    @JoinColumn(name = "saleOrder_id")
    @JsonIgnore
    SaleOrder saleOrder;

    @ManyToOne
    @JoinColumn(name = "vehicle_id")
    @JsonIgnore
    ElectricVehicle vehicle;

}
