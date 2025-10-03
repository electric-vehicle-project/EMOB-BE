package com.example.emob.model.request.schedule;

import com.example.emob.constant.TestStatus;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UpdateTestDriveRequest {
    UUID salePersonId;
    String location;
    int duration;
    LocalDateTime scheduleDate;
    TestStatus status;
}
