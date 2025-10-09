package com.example.emob.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.UUID;

@Entity
@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class QuotationItem {
    @Id
    @GeneratedValue
    UUID id;

    double unitPrice;
    double discount;
    double totalPrice;
    int quantity;
    String color;

    @ManyToOne
    @JoinColumn(name = "quotation_id")
    @JsonIgnore
    Quotation quotation;

    @ManyToOne
    @JoinColumn(name = "vehicle_id")
    @JsonIgnore
    ElectricVehicle vehicle;
}
