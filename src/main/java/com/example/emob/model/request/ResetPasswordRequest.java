/* EMOB-2025 */
package com.example.emob.model.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ResetPasswordRequest {
  @NotBlank(message = "FIELD_REQUIRED")
  @Size(min = 8, max = 50, message = "INVALID_SIZE_PASSWORD")
  @Pattern(
      regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]+$",
      message = "INVALID_PATTERN_PASSWORD")
  String newPassword;
}
