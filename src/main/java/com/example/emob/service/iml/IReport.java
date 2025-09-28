package com.example.emob.service.iml;

import com.example.emob.model.request.report.CreateReportRequest;
import com.example.emob.model.response.APIResponse;
import com.example.emob.model.response.ReportResponse;

public interface IReport {
    APIResponse<ReportResponse> createReport (CreateReportRequest request);

//    APIResponse<ReportResponse> updateReport (ReportRequest request);
}
