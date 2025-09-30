package com.example.emob.model.response;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class DealerResponse {
    UUID id;
    String name;
    String contactInfo;
    String country;
    LocalDateTime createdAt;
}
