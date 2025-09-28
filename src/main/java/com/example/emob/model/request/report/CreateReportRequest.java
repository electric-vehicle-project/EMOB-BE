package com.example.emob.model.request.report;

import com.example.emob.constant.ReportStatus;
import com.example.emob.entity.Customer;
import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.Enumerated;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.UUID;

import static com.example.emob.constant.ReportStatus.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CreateReportRequest {
    String description;
    String title;
    Customer customer;
}
