/* EMOB-2025 */
package com.example.emob.model.response;

import com.example.emob.constant.AccountStatus;
import com.example.emob.constant.Gender;
import com.example.emob.constant.Role;
import com.fasterxml.jackson.annotation.JsonInclude;
import java.time.LocalDate;
import java.util.UUID;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class AccountResponse {
    UUID id;
    String fullName;
    Gender gender;
    AccountStatus status;
    String address;
    LocalDate dateOfBirth;
    Role role;
    String phone;
    String email;
    String token;
    String refreshToken;
}
