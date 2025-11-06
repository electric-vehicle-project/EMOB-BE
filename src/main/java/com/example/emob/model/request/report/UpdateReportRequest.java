/* EMOB-2025 */
package com.example.emob.model.request.report;

import com.example.emob.constant.ReportStatus;
import com.example.emob.constant.ReportType;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class UpdateReportRequest {
  String title;
  String description;
  String vinNumber;
  ReportStatus status;
  ReportType type;
}
