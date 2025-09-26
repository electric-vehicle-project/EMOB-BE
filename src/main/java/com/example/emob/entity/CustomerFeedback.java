package com.example.emob.entity;

import com.example.emob.constant.CustomerStatus;
import com.example.emob.constant.Gender;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.sql.Date;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Builder

@Table(name = "customer_feedback")
public class CustomerFeedback {
    @Id
    @GeneratedValue
    @Column(columnDefinition = "BINARY(16)", unique = true, updatable = false, nullable = false)
    UUID customerId;
    String fullName;
    @Column(unique = true)
    String email;
    @Column(unique = true)
    String phoneNumber;
    String address;

    Date dateOfBirth;

    @Enumerated(EnumType.STRING)
    Gender gender;

    int age;

    String content;

    @Enumerated(EnumType.STRING)
    CustomerStatus status;

    LocalDateTime createdAt;
    LocalDateTime updatedAt;
}
