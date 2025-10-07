/* EMOB-2025 */
package com.example.emob.model.request.schedule;

import com.example.emob.constant.TestStatus;
<<<<<<< HEAD
import jakarta.validation.constraints.NotNull;
import lombok.*;
import lombok.experimental.FieldDefaults;

=======
>>>>>>> f514e41d121209766b1808e639b623d8b269ae3d
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
    UUID vehicleUnitId;
    String location;
    TestStatus status;
    int duration;
    @NotNull
    LocalDateTime scheduledAt;
}
