/* EMOB-2025 */
package com.example.emob.model.request;

import com.example.emob.constant.AccountStatus;
import com.example.emob.constant.Gender;
import com.example.emob.constant.Role;
import jakarta.validation.constraints.Email;
import java.time.LocalDate;
import lombok.*;
import lombok.experimental.FieldDefaults;

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
  @Email String email;
  String password;
}
