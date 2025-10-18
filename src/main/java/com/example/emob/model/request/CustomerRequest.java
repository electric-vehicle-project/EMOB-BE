/* EMOB-2025 */
package com.example.emob.model.request;

import com.example.emob.constant.Gender;
import com.example.emob.validator.EnumValidator;
import jakarta.validation.constraints.*;
import java.time.LocalDate;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CustomerRequest {
  @NotBlank(message = "FIELD_REQUIRED")
  @Size(min = 2, max = 50, message = "INVALID_SIZE_FULL_NAME")
  String fullName;

  @NotBlank(message = "FIELD_REQUIRED")
  @Email(message = "INVALID_EMAIL")
  String email;

  @NotBlank(message = "FIELD_REQUIRED")
  @Pattern(regexp = "^(0[0-9]{9,10})$", message = "INVALID_PHONE_NUMBER")
  String phoneNumber;

  @NotBlank(message = "FIELD_REQUIRED")
  String address;

  String note;

  @Past(message = "INVALID_DATE")
  LocalDate dateOfBirth;

  @NotNull(message = "FIELD_REQUIRED")
  @EnumValidator(enumClass = Gender.class)
  Gender gender;

  int loyaltyPoints;
}
