/* EMOB-2025 */
package com.example.emob.model.request;

import jakarta.validation.constraints.NotBlank;
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
  String contactInfo;

  @NotBlank(message = "FIELD_REQUIRED")
  String country;

  @NotBlank(message = "FIELD_REQUIRED")
  String address;
}
