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
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Component
public class LoginRequest {
    @Email(message = "INVALID_EMAIL")
    @Column(unique = true)
    @Schema(example = "Email is invalid")
    String email;
    @Size(min = 6, message = "PASSWORD_TOO_SHORT")
    @Schema(example = "Password must be longer than 6 digits")
    String password;
}
