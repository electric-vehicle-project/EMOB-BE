/* EMOB-2025 */
package com.example.emob.model.request;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class DealerRequest {
  String name;

  String contactInfo;

  String country;
}
