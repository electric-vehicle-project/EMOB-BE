package com.example.emob.model.response;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;
import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class DecodedToken {
     String iss;
     Object aud;  // có thể là String hoặc List<String>
     String sub;
     String jti;
     String tokenType;
     String roles;
     Date iat;
     Date exp;
}
