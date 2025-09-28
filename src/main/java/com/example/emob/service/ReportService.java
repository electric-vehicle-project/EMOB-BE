package com.example.emob.service;

import com.example.emob.constant.ErrorCode;
import com.example.emob.constant.ReportStatus;
import com.example.emob.constant.ReportType;
import com.example.emob.entity.Customer;
import com.example.emob.entity.Report;
import com.example.emob.exception.GlobalException;
import com.example.emob.mapper.CustomerMapper;
import com.example.emob.mapper.ReportMapper;
import com.example.emob.model.request.report.CreateReportRequest;
import com.example.emob.model.request.report.UpdateReportRequest;
import com.example.emob.model.response.APIResponse;
import com.example.emob.model.response.ReportResponse;
import com.example.emob.repository.CustomerRepository;
import com.example.emob.repository.ReportRepository;
import com.example.emob.service.iml.IReport;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Service
public class ReportService implements IReport {
    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private CustomerMapper customerMapper;

    @Autowired
    private ReportMapper reportMapper;

    @Autowired
    private ReportRepository reportRepository;

    @Override
    public APIResponse<ReportResponse> createReport(CreateReportRequest request) {
        Customer customer = customerRepository.findCustomerById(request.getCustomer().getId());
        System.out.println("Customer ID from request = " + request.getCustomer().getId());
        if (customer == null) {
            throw new GlobalException(ErrorCode.NOT_FOUND);
        }
        try {
                Report report = reportMapper.toReport(request);
                report.setReportBy(customer);
                report.setType(ReportType.FEEDBACK);
                report.setTitle(request.getTitle());
                report.setDescription(request.getDescription());
                report.setStatus(ReportStatus.PENDING);
                report.setCreatedAt(LocalDateTime.now());
                reportRepository.save(report);
                // create new report
                ReportResponse reportResponse = reportMapper.toReportResponse(report);
                APIResponse<ReportResponse> apiResponse = new APIResponse<>();
                apiResponse.setMessage("Create report successfully");
                apiResponse.setResult(reportResponse);
                return apiResponse;
        } catch (Exception ex) {
            throw new GlobalException(ErrorCode.OTHER);
        }
    }

    @Override
    public APIResponse<ReportResponse> updateReport(UpdateReportRequest request, UUID reportId) {
        Report report = reportRepository.findReportByReportId(reportId);
        if (report == null) {
            throw new GlobalException(ErrorCode.NOT_FOUND);
        }
        try {
            report.setStatus(request.getStatus());
            report.setTitle(request.getTitle());
            report.setDescription(request.getDescription());
            report.setUpdatedAt(LocalDateTime.now());
            reportRepository.save(report);
            ReportResponse reportResponse = reportMapper.toReportResponse(report);
            APIResponse<ReportResponse> apiResponse = new APIResponse<>();
            apiResponse.setMessage("Update report successfully");
            apiResponse.setResult(reportResponse);
            return apiResponse;
        } catch (Exception ex) {
            throw new GlobalException(ErrorCode.OTHER);
        }
    }
}
