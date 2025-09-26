package com.example.emob.model.response;


import com.example.emob.constant.ReportType;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CustomerFeedbackResponse {
    UUID id;
    String content;
    String email;
    String phone;
    String fullName;
    ReportType type;
}
