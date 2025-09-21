package com.example.emob.model.request;

import com.example.emob.constant.AccountStatus;
import com.example.emob.constant.Gender;
import com.example.emob.constant.Role;
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
public class RegisterRequest {
    String email;
    String password;
    Gender gender;
    AccountStatus status;
    String phone;
    Role role;
}
