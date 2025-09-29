package com.example.emob.service.iml;

import com.example.emob.constant.ReportStatus;
import com.example.emob.model.request.report.CreateReportRequest;
import com.example.emob.model.request.report.UpdateReportRequest;
import com.example.emob.model.response.APIResponse;
import com.example.emob.model.response.ReportResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface IReport {
    // ghi nhận
    APIResponse<ReportResponse> createReport (CreateReportRequest request);

    APIResponse<ReportResponse> updateReport (UpdateReportRequest request, UUID reportId);

    APIResponse<ReportResponse> deleteReport (UUID reportId);

    APIResponse<ReportResponse> viewReport (UUID reportId);

    APIResponse<Page<ReportResponse>> viewAllReport (Pageable pageable);

    // xử lý
    APIResponse<ReportResponse> changeStatus (UUID id, ReportStatus status);

}
