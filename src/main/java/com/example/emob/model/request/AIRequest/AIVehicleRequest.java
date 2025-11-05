/* EMOB-2025 */
package com.example.emob.model.request.AIRequest;

import java.util.List;
import java.util.Map;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class AIVehicleRequest {
  String modelName;
  List<Map<String, Object>> data;
}
