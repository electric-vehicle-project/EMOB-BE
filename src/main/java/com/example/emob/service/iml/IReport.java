package com.example.emob.service.iml;

import com.example.emob.model.request.ReportRequest;
import com.example.emob.model.response.APIResponse;
import com.example.emob.model.response.ReportResponse;

public interface IReport {
    APIResponse<ReportResponse> createReport (ReportRequest request);

//    APIResponse<ReportResponse> updateReport (ReportRequest request);
}
