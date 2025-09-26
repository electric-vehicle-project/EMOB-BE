package com.example.emob.model.response;


import com.example.emob.constant.ReportType;
import com.example.emob.entity.CustomerFeedback;
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
public class ReportResponse {
    UUID id;
    String title;
    String content;
    ReportType type;
    CustomerFeedback reportBy;
}
