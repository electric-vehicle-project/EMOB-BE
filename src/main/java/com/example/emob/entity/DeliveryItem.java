package com.example.emob.entity;

import com.example.emob.constant.DeliveryItemStatus;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.UuidGenerator;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class DeliveryItem {
    @Id
    @UuidGenerator
    UUID id;
    @Enumerated(EnumType.STRING)
    DeliveryItemStatus status;
    String remarks;

    int quantity;

    LocalDateTime createAt;
    LocalDateTime updateAt;

    LocalDateTime confirmAt;

    @ManyToOne
    @JoinColumn(name = "delivery_id")
    @JsonIgnore
    Delivery delivery;

    @OneToOne
    @JoinColumn(name = "vehicle_unit_id")
    @JsonIgnore
    VehicleUnit vehicleUnit;
}
