package com.example.emob.entity;

import com.example.emob.constant.TestStatus;
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
@Table(name = "TestDrive")
public class TestDrive {
    @Id
    @GeneratedValue
    @Column(columnDefinition = "BINARY(16)", unique = true)
    UUID testId;

    String location;

    @Enumerated(EnumType.STRING)
    TestStatus status;

    @ManyToOne
    @JoinColumn(name = "salePersonId", referencedColumnName = "id")
    Account salePersonId;

    @ManyToOne
    @JoinColumn(name = "customerId", referencedColumnName = "id")
    Customer customerId;

    int duration;
}
