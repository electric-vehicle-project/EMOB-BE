package com.example.emob.model.request.schedule;

import com.example.emob.constant.TestStatus;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ViewAllTestDriveRequest {
    String keyword;
    TestStatus status;
}
