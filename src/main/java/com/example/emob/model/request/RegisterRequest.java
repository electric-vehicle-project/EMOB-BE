package com.example.emob.model.request;

import com.example.emob.constant.AccountStatus;
import com.example.emob.constant.Gender;
import com.example.emob.constant.Role;
import jakarta.persistence.Column;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.validation.constraints.Email;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.UUID;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class RegisterRequest {

    String fullName;
    Gender gender;
    AccountStatus status;
    String address;
    LocalDate dateOfBirth;
    Role role;
    String phone;
    @Email
    String email;
    String password;
}
