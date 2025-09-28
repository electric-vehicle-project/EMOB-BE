package com.example.emob.model.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.Column;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Component;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class LoginRequest {
    @Email(message = "INVALID_EMAIL")
    @Schema(
            description = "User email"

    )
    String email;

    @Size(min = 6, message = "PASSWORD_TOO_SHORT")
    @Schema(
            description = "User password"

    )
    String password;
}
