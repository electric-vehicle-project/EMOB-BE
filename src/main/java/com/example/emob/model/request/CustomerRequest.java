package com.example.emob.model.request;

import com.example.emob.constant.Gender;
import jakarta.persistence.Column;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CustomerRequest {
    String fullName;
    String email;
    String phoneNumber;
    String address;
    String note;
    LocalDate dateOfBirth;
    Gender gender;
    int loyaltyPoints;

}
