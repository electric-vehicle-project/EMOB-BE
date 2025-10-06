/* EMOB-2025 */
package com.example.emob.model.response;

import com.example.emob.constant.TestStatus;
import com.example.emob.entity.Customer;
import com.fasterxml.jackson.annotation.JsonInclude;
import java.time.LocalDateTime;
import java.util.UUID;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class TestDriveResponse {
    UUID testDriveId;
    UUID salePersonId;
    LocalDateTime scheduledAt;
    int duration;
    String location;
    TestStatus status;
    Customer customer;
    LocalDateTime createAt;
    LocalDateTime updateAt;
}
