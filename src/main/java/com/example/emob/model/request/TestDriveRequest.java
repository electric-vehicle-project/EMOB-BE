/* EMOB-2025 */
package com.example.emob.model.request;

import java.time.LocalDateTime;
import java.util.UUID;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class TestDriveRequest {
  UUID customerId;
  UUID testDriveVehicleId;
  String location;
  int duration;
  LocalDateTime scheduledAt;
}
