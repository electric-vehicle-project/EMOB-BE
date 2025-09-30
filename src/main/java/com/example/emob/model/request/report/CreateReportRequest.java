package com.example.emob.model.request.report;

import com.example.emob.constant.ReportStatus;
import com.example.emob.constant.ReportType;
import com.example.emob.entity.Customer;
import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.UUID;


@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CreateReportRequest {
    @NotNull(message = "AccountId is required")
    UUID accountId;
    @NotNull(message = "CustomerId is required")
    UUID customerId;
    @NotBlank(message = "Description is required")
    String description;
    @NotBlank(message = "Title is required")
    String title;
    ReportStatus status;
    ReportType type;
}
