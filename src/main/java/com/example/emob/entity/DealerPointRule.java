/* EMOB-2025 */
package com.example.emob.entity;

import java.math.BigDecimal;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.index.Indexed;

@RedisHash("dealer_point") // key prefix trong Redis
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class DealerPointRule {
  @Id String membershipLevel;
  @Indexed String dealerId;
  int minPoints;
  BigDecimal price;
}
