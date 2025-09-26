package com.example.emob.model.request;

import com.example.emob.constant.ReportType;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Component;

@Data
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Builder
@Component
public class CustomerFeedbackRequest {
     String content;
     @Email(message = "INVALID_EMAIL")
    @Schema(example = "Email is required")
     String email;
    @Schema(example = "Phone is required")
     String phone;
    @Schema(example = "Full name is required")
     String fulName;

    ReportType type;

}
