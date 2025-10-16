/* EMOB-2025 */
package com.example.emob.entity;

import lombok.*;
import lombok.experimental.FieldDefaults;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;

@RedisHash("vehiclePriceRule") // key prefix trong Redis
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class VehiclePriceRule {
  @Id String vehicleStatus; // REAL, TEST_DRIVE, SPECIAL...

  Double multiplier; // Hệ số giá
  String note;
}
