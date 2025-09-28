package com.example.emob.service.iml;

import com.example.emob.model.request.report.CreateReportRequest;
import com.example.emob.model.request.report.UpdateReportRequest;
import com.example.emob.model.response.APIResponse;
import com.example.emob.model.response.ReportResponse;

import java.util.UUID;

public interface IReport {
    APIResponse<ReportResponse> createReport (CreateReportRequest request);

    APIResponse<ReportResponse> updateReport (UpdateReportRequest request, UUID reportId);

    APIResponse<ReportResponse> deleteReport (UUID reportId);
}
