/* EMOB-2025 */
package com.example.emob.model.request.dealerDiscountPolicy;

import java.util.List;
import java.util.UUID;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class DealerDiscountPolicyBulkDeleteRequest {
  @NotEmpty(message = "FIELD_REQUIRED")
  List<UUID> dealerIds; // danh sách ID đại lý
  @NotEmpty(message = "FIELD_REQUIRED")
  List<UUID> vehicleModelIds; // danh sách ID loại xe
}
