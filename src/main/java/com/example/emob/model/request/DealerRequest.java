/* EMOB-2025 */
package com.example.emob.model.request;

import com.example.emob.constant.Region;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class DealerRequest {
  @NotBlank(message = "FIELD_REQUIRED")
  String name;

  @NotBlank(message = "FIELD_REQUIRED")
  @Email(message = "INVALID_EMAIL")
  String emailContact;

  @NotNull(message = "FIELD_REQUIRED")
  Region region;

  @NotBlank(message = "FIELD_REQUIRED")
  String phoneContact;

  @NotBlank(message = "FIELD_REQUIRED")
  String country;

  @NotBlank(message = "FIELD_REQUIRED")
  String address;
}
