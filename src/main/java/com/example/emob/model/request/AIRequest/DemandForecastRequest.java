/* EMOB-2025 */
package com.example.emob.model.request.AIRequest;

import com.example.emob.constant.Region;
import java.util.Set;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class DemandForecastRequest {
  String country;
  Region region;
  Set<AIVehicleRequest> vehicles;
  String timeRange;
}
