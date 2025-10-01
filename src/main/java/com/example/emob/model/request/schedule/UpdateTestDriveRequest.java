package com.example.emob.model.request.schedule;

import com.example.emob.constant.TestStatus;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UpdateTestDriveRequest {
    String location;
    int duration;
    LocalDateTime scheduleDate;
    TestStatus status;
}
