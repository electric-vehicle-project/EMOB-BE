package com.example.emob.service;

import com.example.emob.constant.ErrorCode;
import com.example.emob.constant.ReportStatus;
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
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
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
        Customer customer = customerRepository.findById(request.getCustomerId())
                .orElseThrow(() -> new GlobalException(ErrorCode.NOT_FOUND));
        try {
            Report report = reportMapper.toReport(request);
            report.setStatus(ReportStatus.PENDING);
            report.setCreatedAt(LocalDateTime.now());
            report.setReportBy(customer);
            reportRepository.save(report);
            // create new report
            ReportResponse reportResponse = reportMapper.toReportResponse(report);
            reportResponse.setReportId(report.getId());
            APIResponse<ReportResponse> apiResponse = new APIResponse<>();
            apiResponse.setResult(reportResponse);
            apiResponse.setMessage("Create report successfully");
            return apiResponse;
        } catch (DataIntegrityViolationException ex) {
            throw new GlobalException(ErrorCode.DATA_INVALID);
        } catch (DataAccessException ex) {
            throw new GlobalException(ErrorCode.DB_ERROR);
        } catch (Exception ex) {
            throw new GlobalException(ErrorCode.OTHER);
        }
    }

    @Override
    public APIResponse<ReportResponse> updateReport(UpdateReportRequest request, UUID reportId) {
        Report report = reportRepository.findById(reportId).
                orElseThrow(() -> new GlobalException(ErrorCode.NOT_FOUND));
        try {
            // Map request -> entity
            reportMapper.updateReportFromRequest(request, report);
            report.setUpdatedAt(LocalDateTime.now());
            reportRepository.save(report);
            ReportResponse reportResponse = reportMapper.toReportResponse(report);
            reportResponse.setReportId(report.getId());
            APIResponse<ReportResponse> apiResponse = new APIResponse<>();
            apiResponse.setMessage("Update report successfully");
            apiResponse.setResult(reportResponse);
            return apiResponse;

        } catch (DataIntegrityViolationException ex) {
            throw new GlobalException(ErrorCode.DATA_INVALID);
        } catch (DataAccessException ex) {
            throw new GlobalException(ErrorCode.DB_ERROR);
        } catch (Exception ex) {
            throw new GlobalException(ErrorCode.OTHER);
        }
    }

    @Override
    public APIResponse<ReportResponse> deleteReport(UUID reportId) {
        Report report = reportRepository.findById(reportId).
                orElseThrow(() -> new GlobalException(ErrorCode.NOT_FOUND));
        try {
            report.setStatus(ReportStatus.DELETED);
            reportRepository.save(report);
            ReportResponse reportResponse = reportMapper.toReportResponse(report);
            reportResponse.setReportId(report.getId());
            APIResponse<ReportResponse> apiResponse = new APIResponse<>();
            apiResponse.setMessage("Delete report successfully");
            apiResponse.setResult(reportResponse);
            return apiResponse;
        } catch (DataIntegrityViolationException ex) {
            throw new GlobalException(ErrorCode.DATA_INVALID);
        } catch (DataAccessException ex) {
            throw new GlobalException(ErrorCode.DB_ERROR);
        } catch (Exception ex) {
            throw new GlobalException(ErrorCode.OTHER);
        }
    }

    @Override
    public APIResponse<ReportResponse> viewReport(UUID reportId) {
        Report report = reportRepository.findById(reportId).
                orElseThrow(() -> new GlobalException(ErrorCode.NOT_FOUND));
        ReportResponse reportResponse = reportMapper.toReportResponse(report);
        reportResponse.setReportId(report.getId());
        APIResponse<ReportResponse> apiResponse = new APIResponse<>();
        apiResponse.setMessage("View report successfully");
        apiResponse.setResult(reportResponse);
        return apiResponse;
    }

    // phân trang
    @PreAuthorize("hasAnyRole('Admin', 'Manager')")
    @Override
    public APIResponse<Page<ReportResponse>> viewAllReport(Pageable pageable) {
        Page<Report> reports = reportRepository.findAll(pageable);
        // map từng Report trong trang thành ReportResponse
        Page<ReportResponse> responses = reports.map(reportMapper::toReportResponse);
        APIResponse<Page<ReportResponse>> apiResponse = new APIResponse<>();
        apiResponse.setResult(responses);
        apiResponse.setMessage("View all reports successfully");
        return apiResponse;
    }

    @Override
    public APIResponse<ReportResponse> changeStatus(UUID reportId, ReportStatus status) {
        Report report = reportRepository.findById(reportId).
                orElseThrow(() -> new GlobalException(ErrorCode.NOT_FOUND));
        try {
            // IN_PROGRESS | RESOLVED
            report.setStatus(status);
            report.setUpdatedAt(LocalDateTime.now());
            reportRepository.save(report);

            ReportResponse reportResponse = reportMapper.toReportResponse(report);
            reportResponse.setReportId(report.getId());
            APIResponse<ReportResponse> apiResponse = new APIResponse<>();
            apiResponse.setResult(reportResponse);
            apiResponse.setMessage("Change status successfully");
            return apiResponse;
        } catch (DataIntegrityViolationException ex) {
            throw new GlobalException(ErrorCode.DATA_INVALID);
        } catch (DataAccessException ex) {
            throw new GlobalException(ErrorCode.DB_ERROR);
        } catch (Exception ex) {
            throw new GlobalException(ErrorCode.OTHER);
        }
    }
}
