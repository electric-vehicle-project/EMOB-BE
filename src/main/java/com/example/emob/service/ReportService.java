/* EMOB-2025 */
package com.example.emob.service;

import com.example.emob.constant.ErrorCode;
import com.example.emob.constant.ReportStatus;
import com.example.emob.entity.Account;
import com.example.emob.entity.Customer;
import com.example.emob.entity.Report;
import com.example.emob.exception.GlobalException;
import com.example.emob.mapper.CustomerMapper;
import com.example.emob.mapper.PageMapper;
import com.example.emob.mapper.ReportMapper;
import com.example.emob.model.request.report.CreateReportRequest;
import com.example.emob.model.request.report.UpdateReportRequest;
import com.example.emob.model.response.APIResponse;
import com.example.emob.model.response.PageResponse;
import com.example.emob.model.response.ReportResponse;
import com.example.emob.repository.AccountRepository;
import com.example.emob.repository.CustomerRepository;
import com.example.emob.repository.ReportRepository;
import com.example.emob.service.iml.IReport;
import java.time.LocalDateTime;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class ReportService implements IReport {
    @Autowired private CustomerRepository customerRepository;

    @Autowired private CustomerMapper customerMapper;

    @Autowired private ReportMapper reportMapper;

    @Autowired private ReportRepository reportRepository;

    @Autowired private PageMapper pageMapper;

    @Autowired private AccountRepository accountRepository;

    @Override
    public APIResponse<ReportResponse> createReport(CreateReportRequest request) {
        // khách hàng
        Customer customer =
                customerRepository
                        .findById(request.getCustomerId())
                        .orElseThrow(() -> new GlobalException(ErrorCode.NOT_FOUND));
        // nhân viên nào tạo
        Account account =
                accountRepository
                        .findById(request.getAccountId())
                        .orElseThrow(() -> new GlobalException(ErrorCode.NOT_FOUND));
        try {
            Report report = reportMapper.toReport(request);
            report.setStatus(ReportStatus.PENDING);
            report.setCreatedAt(LocalDateTime.now());
            report.setCreateBy(account);
            report.setReportBy(customer);
            reportRepository.save(report);
            // create new report
            ReportResponse reportResponse = reportMapper.toReportResponse(report);
            return APIResponse.success(reportResponse, "Create report successfully");
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
        Report report =
                reportRepository
                        .findById(reportId)
                        .orElseThrow(() -> new GlobalException(ErrorCode.NOT_FOUND));

        try {
            // Map request -> entity
            reportMapper.updateReportFromRequest(request, report);
            report.setUpdatedAt(LocalDateTime.now());
            reportRepository.save(report);
            ReportResponse reportResponse = reportMapper.toReportResponse(report);
            return APIResponse.success(reportResponse, "Update report successfully");
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
        Report report =
                reportRepository
                        .findById(reportId)
                        .orElseThrow(() -> new GlobalException(ErrorCode.NOT_FOUND));
        try {
            report.setStatus(ReportStatus.DELETED);
            reportRepository.save(report);
            ReportResponse reportResponse = reportMapper.toReportResponse(report);
            return APIResponse.success(reportResponse, "Delete report successfully");
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
        Report report =
                reportRepository
                        .findById(reportId)
                        .orElseThrow(() -> new GlobalException(ErrorCode.NOT_FOUND));
        ReportResponse reportResponse = reportMapper.toReportResponse(report);
        return APIResponse.success(reportResponse, "View report successfully");
    }

    // phân trang
    @Override
    public APIResponse<PageResponse<ReportResponse>> viewAllReport(Pageable pageable) {
        Page<Report> reports = reportRepository.findAll(pageable);
        // map từng Report trong trang thành ReportResponse
        PageResponse<ReportResponse> responses =
                pageMapper.toPageResponse(reports, reportMapper::toReportResponse);
        return APIResponse.success(responses, "View all reports successfully");
    }

    @Override
    public APIResponse<ReportResponse> changeStatus(UUID reportId, ReportStatus status) {
        Report report =
                reportRepository
                        .findById(reportId)
                        .orElseThrow(() -> new GlobalException(ErrorCode.NOT_FOUND));
        try {
            // IN_PROGRESS | RESOLVED
            report.setStatus(status);
            report.setUpdatedAt(LocalDateTime.now());
            reportRepository.save(report);

            ReportResponse reportResponse = reportMapper.toReportResponse(report);
            //            reportResponse.setReportId(report.getId());
            return APIResponse.success(reportResponse, "Change status successfully");
        } catch (DataIntegrityViolationException ex) {
            throw new GlobalException(ErrorCode.DATA_INVALID);
        } catch (DataAccessException ex) {
            throw new GlobalException(ErrorCode.DB_ERROR);
        } catch (Exception ex) {
            throw new GlobalException(ErrorCode.OTHER);
        }
    }
}
