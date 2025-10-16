/* EMOB-2025 */
package com.example.emob.model.request.promotion;

import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.util.Set;
import java.util.UUID;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PromotionRequest {
  Set<UUID> dealerId;

  @NotEmpty(message = "Electric vehicle IDs cannot be empty")
  Set<UUID> electricVehiclesId;

  @NotNull(message = "Name cannot be null")
  @Size(min = 2, max = 100, message = "Name must be between 2 and 100 characters")
  String name;

  @Size(max = 255, message = "Description must not exceed 255 characters")
  String description;
}
