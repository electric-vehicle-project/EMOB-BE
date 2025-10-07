/* EMOB-2025 */
package com.example.emob.entity;

import java.io.Serializable;
import java.time.Instant;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.TimeToLive;

@RedisHash("refreshToken") // key prefix trong Redis
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class RefreshToken implements Serializable {

    @Id String token;

    String accountId;
    Instant issuedAt;
    boolean isRevoked;
    @TimeToLive Long ttl;
}
