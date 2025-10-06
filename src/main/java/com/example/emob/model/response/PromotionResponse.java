/* EMOB-2025 */
package com.example.emob.model.response;

import com.example.emob.constant.MemberShipLevel;
import com.example.emob.constant.PromotionScope;
import com.example.emob.constant.PromotionStatus;
import com.example.emob.constant.PromotionType;
import java.time.LocalDateTime;
import java.util.Set;
import java.util.UUID;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class PromotionResponse {
    UUID promotionId;
    UUID staffId;
    Set<UUID> dealerId;
    Set<UUID> vehicleId;
    String name;
    String description;
    PromotionType type;
    float value;
    float minValue;
    LocalDateTime startDate;
    LocalDateTime endDate;
    PromotionScope scope;
    PromotionStatus status;
    LocalDateTime createAt;
    MemberShipLevel memberShipLevel;
}
