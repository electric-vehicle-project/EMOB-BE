/* EMOB-2025 */
package com.example.emob.model.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class OtpRequest {
  @NotBlank(message = "FIELD_REQUIRED")
  @Email(message = "INVALID_EMAIL")
  String email;
}
