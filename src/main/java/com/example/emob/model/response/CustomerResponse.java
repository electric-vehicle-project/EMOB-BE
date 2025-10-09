package com.example.emob.model.response;

import com.example.emob.constant.Gender;
import com.example.emob.constant.MemberShipLevel;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CustomerResponse {
    String fullName;
    String email;
    String phoneNumber;
    String address;
    String note;
    LocalDate dateOfBirth;
    Gender gender;
    int loyaltyPoints;
    MemberShipLevel memberShipLevel;
}
