/* EMOB-2025 */
package com.example.emob.entity;

import java.io.Serializable;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.TimeToLive;
import org.springframework.data.redis.core.index.Indexed;

@RedisHash(value = "otp") // key prefix trong Redis
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Otp implements Serializable {
  @Id
  @Indexed
  String accountId;
  String otp;
  @TimeToLive Long ttl;
  String token;
  int resendCount; // số lần gửi lại otp
}
