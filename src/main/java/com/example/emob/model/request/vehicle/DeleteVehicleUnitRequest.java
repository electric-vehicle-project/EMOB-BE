/* EMOB-2025 */
package com.example.emob.model.request.vehicle;

import java.util.List;
import java.util.UUID;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class DeleteVehicleUnitRequest {
  List<UUID> vehicleUnitIds;
}
