package com.example.emob.model.request.schedule;

import com.example.emob.constant.TestStatus;
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
  UUID accountId;
  String location;
  TestStatus status;
  int duration;
  LocalDateTime scheduledAt;
}
