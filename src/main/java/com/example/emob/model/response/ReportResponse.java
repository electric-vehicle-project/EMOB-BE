/* EMOB-2025 */
package com.example.emob.model.response;

import com.example.emob.constant.ReportStatus;
import com.example.emob.constant.ReportType;
import com.fasterxml.jackson.annotation.JsonInclude;
import java.time.LocalDateTime;
import java.util.UUID;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ReportResponse {
  UUID reportId;
  String title;
  String description;
  ReportType type;
  ReportStatus status;
  UUID customerId;
  String fullName;
  LocalDateTime createdAt;
  LocalDateTime updatedAt;
  String solution;
}
