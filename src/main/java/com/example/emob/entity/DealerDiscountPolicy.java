package com.example.emob.entity;

import com.example.emob.constant.DiscountPolicyStatus;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Builder
public class DealerDiscountPolicy {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(columnDefinition = "BINARY(16)", unique = true, updatable = false, nullable = false)
    UUID id;
    double customMultiplier;
    LocalDateTime createAt;
    LocalDateTime updateAt;
    LocalDate effectiveDate;
    LocalDate expiryDate;
    BigDecimal finalPrice;      // nếu muốn chốt giá cố định
    @Enumerated(EnumType.STRING)
    DiscountPolicyStatus status;
    @ManyToOne
    @JoinColumn(name = "dealer_id")
    Dealer dealer;
    @ManyToOne
    @JoinColumn(name = "Vehicle_id")
    ElectricVehicle vehicle;
}
