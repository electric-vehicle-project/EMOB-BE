/* EMOB-2025 */
package com.example.emob.model.request.vehicleRequest;

import java.util.List;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class VehicleRequestRequest<T> {
  List<T> items;
}
