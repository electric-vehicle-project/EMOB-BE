/* EMOB-2025 */
package com.example.emob.service.impl;

import com.example.emob.constant.ReportStatus;
import com.example.emob.model.request.report.CreateReportRequest;
import com.example.emob.model.request.report.UpdateReportRequest;
import com.example.emob.model.response.APIResponse;
import com.example.emob.model.response.PageResponse;
import com.example.emob.model.response.ReportResponse;
import java.util.UUID;
import org.springframework.data.domain.Pageable;

public interface IReport {
  // ghi nhận
  APIResponse<ReportResponse> createReport(CreateReportRequest request);

  APIResponse<ReportResponse> updateReport(UpdateReportRequest request, UUID reportId);

  APIResponse<ReportResponse> deleteReport(UUID reportId);

  APIResponse<ReportResponse> viewReport(UUID reportId);

  APIResponse<PageResponse<ReportResponse>> viewAllReport(Pageable pageable);

  // xử lý
  APIResponse<ReportResponse> changeStatus(UUID id, ReportStatus status);
}
