package com.example.emob.entity;

import com.example.emob.constant.CustomerStatus;
import com.example.emob.constant.Gender;

import com.example.emob.constant.MemberShipLevel;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Builder
public class Customer {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(columnDefinition = "BINARY(16)", unique = true, updatable = false, nullable = false)
    UUID id;
    String fullName;
    @Column(unique = true)
    String email;
    
    @Column(unique = true)
    String phoneNumber;
    String address;
    String note;
    LocalDate dateOfBirth;

    @Enumerated(EnumType.STRING)
    Gender gender;

    @Enumerated(EnumType.STRING)
    CustomerStatus status;

    @OneToMany(mappedBy = "reportBy", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnore
    List<Report> reports;

    int loyaltyPoints;

    @Enumerated(EnumType.STRING)
    MemberShipLevel memberShipLevel;
    
}
