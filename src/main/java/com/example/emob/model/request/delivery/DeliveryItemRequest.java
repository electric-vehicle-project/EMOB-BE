/* EMOB-2025 */
package com.example.emob.model.request.delivery;

import com.example.emob.constant.DeliveryItemStatus;
import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.constraints.NotNull;
import java.util.UUID;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class DeliveryItemRequest {
  @NotNull(message = "Vehicle ID cannot be null")
  UUID vehicleId;

  String remarks;
  DeliveryItemStatus status;
}
