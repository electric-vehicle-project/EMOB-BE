package com.example.emob.entity;

import com.example.emob.constant.TestStatus;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Table(name = "TestDrive")
public class TestDrive {
    @Id
    @GeneratedValue
    @Column(columnDefinition = "BINARY(16)", unique = true)
    UUID id;

    String location;

    @Enumerated(EnumType.STRING)
    TestStatus status;

    @ManyToOne
    @JoinColumn(name = "salesperson", referencedColumnName = "id")
    Account salesperson;

    @ManyToOne
    @JoinColumn(name = "customer", referencedColumnName = "id")
    Customer customer;

    int duration;

    LocalDateTime scheduledAt;

    LocalDateTime createAt;
    LocalDateTime updateAt;

    @ManyToOne
    @JoinColumn(name = "vehicleUnit", referencedColumnName = "id")
    VehicleUnit vehicleUnit;
}
