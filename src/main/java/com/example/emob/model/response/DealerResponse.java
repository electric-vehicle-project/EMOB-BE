/* EMOB-2025 */
package com.example.emob.model.response;

import java.time.LocalDateTime;
import java.util.UUID;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class DealerResponse {
  UUID id;
  String name;
  String contactInfo;
  String country;
  LocalDateTime createdAt;
}
