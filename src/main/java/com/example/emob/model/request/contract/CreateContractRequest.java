/* EMOB-2025 */
package com.example.emob.model.request.contract;

import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.UUID;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CreateContractRequest {
  @NotNull UUID orderId;
  @NotNull LocalDateTime createAt;
}
