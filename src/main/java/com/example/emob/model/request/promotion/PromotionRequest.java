/* EMOB-2025 */
package com.example.emob.model.request.promotion;

import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.constraints.NotNull;
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
    Set<UUID> electricVehiclesId;
    @NotNull String name;
    String description;
}
