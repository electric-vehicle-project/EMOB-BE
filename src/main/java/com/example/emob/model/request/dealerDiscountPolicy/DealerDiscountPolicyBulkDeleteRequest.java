package com.example.emob.model.request.dealerDiscountPolicy;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.List;
import java.util.UUID;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class DealerDiscountPolicyBulkDeleteRequest {
    List<UUID> dealerIds;       // danh sách ID đại lý
    List<UUID> vehicleModelIds; // danh sách ID loại xe
}
