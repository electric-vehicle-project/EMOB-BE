package com.example.emob.service;

import com.example.emob.constant.ErrorCode;
import com.example.emob.constant.ReportType;
import com.example.emob.entity.CustomerFeedback;
import com.example.emob.entity.Report;
import com.example.emob.exception.GlobalException;
import com.example.emob.mapper.CustomerFeedbackMapper;
import com.example.emob.mapper.ReportMapper;
import com.example.emob.model.request.ReportRequest;
import com.example.emob.model.response.APIResponse;
import com.example.emob.model.response.ReportResponse;
import com.example.emob.repository.CustomerFeedbackRepository;
import com.example.emob.repository.ReportRepository;
import com.example.emob.service.iml.IReport;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ReportService implements IReport {
    @Autowired
    private CustomerFeedbackRepository customerFeedbackRepository;

    @Autowired
    private CustomerFeedbackMapper customerFeedbackMapper;

    @Autowired
    private ReportMapper reportMapper;

    @Autowired
    private ReportRepository reportRepository;

    @Override
    public APIResponse<ReportResponse> createReport(ReportRequest request) {
        CustomerFeedback customerFeedback = customerFeedbackRepository.
                findCustomerFeedbackByEmail(request.getReporterEmail());
        System.out.println(customerFeedback.getEmail());
        try {
                Report report = customerFeedbackMapper.toReport(customerFeedback);
                report.setType(ReportType.FEEDBACK);
                report.setReportBy(customerFeedback);
                report.setTitle(request.getTitle());
                report.setContent(request.getContent());
                reportRepository.save(report);
                // create new feedback
                ReportResponse reportResponse = reportMapper.toReportResponse(report);
                APIResponse<ReportResponse> apiResponse = new APIResponse<>();
                apiResponse.setMessage("Create report successfully");
                apiResponse.setResult(reportResponse);
                return apiResponse;
        } catch (Exception ex) {
            throw new GlobalException(ErrorCode.OTHER, "Error: " + ex.getMessage());
        }
    }
}
