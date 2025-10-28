/* EMOB-2025 */
package com.example.emob.service;

import com.example.emob.constant.ErrorCode;
import com.example.emob.constant.ReportStatus;
import com.example.emob.entity.Customer;
import com.example.emob.entity.Report;
import com.example.emob.exception.GlobalException;
import com.example.emob.mapper.CustomerMapper;
import com.example.emob.mapper.PageMapper;
import com.example.emob.mapper.ReportMapper;
import com.example.emob.model.request.report.CreateReportRequest;
import com.example.emob.model.request.report.ResolveReportRequest;
import com.example.emob.model.request.report.UpdateReportRequest;
import com.example.emob.model.response.APIResponse;
import com.example.emob.model.response.PageResponse;
import com.example.emob.model.response.ReportResponse;
import com.example.emob.repository.CustomerRepository;
import com.example.emob.repository.ReportRepository;
import com.example.emob.service.impl.IReport;
import com.example.emob.util.AccountUtil;
import java.time.LocalDateTime;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

@Service
public class ReportService implements IReport {
  @Autowired private CustomerRepository customerRepository;

  @Autowired private CustomerMapper customerMapper;

  @Autowired private ReportMapper reportMapper;

  @Autowired private ReportRepository reportRepository;

  @Autowired private PageMapper pageMapper;

  @Override
  public APIResponse<ReportResponse> createReport(CreateReportRequest request) {
    // khách hàng
    Customer customer =
        customerRepository
            .findById(request.getCustomerId())
            .orElseThrow(() -> new GlobalException(ErrorCode.NOT_FOUND));
    try {
      Report report = reportMapper.toReport(request);
      report.setStatus(ReportStatus.PENDING);
      report.setCreatedAt(LocalDateTime.now());
      report.setDealer(AccountUtil.getCurrentUser().getDealer());
      report.setDealer(AccountUtil.getCurrentUser().getDealer());
      report.setCreateBy(AccountUtil.getCurrentUser());
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
  @PreAuthorize("hasAnyRole('MANAGER' , 'DEALER_STAFF')")
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
  @PreAuthorize("hasAnyRole('MANAGER' , 'DEALER_STAFF')")
  public APIResponse<PageResponse<ReportResponse>> viewAllReport(Pageable pageable) {
    Page<Report> reports =
        reportRepository.findAllByDealer(AccountUtil.getCurrentUser().getDealer(), pageable);
    // map từng Report trong trang thành ReportResponse
    PageResponse<ReportResponse> responses =
        pageMapper.toPageResponse(reports, reportMapper::toReportResponse);
    return APIResponse.success(responses, "View all reports successfully");
  }

  @Override
  public APIResponse<ReportResponse> changeStatus(
      UUID reportId, ReportStatus status, ResolveReportRequest request) {
    Report report =
        reportRepository
            .findById(reportId)
            .orElseThrow(() -> new GlobalException(ErrorCode.NOT_FOUND));
    try {
      if (report.getStatus().equals(status)) {
        throw new GlobalException(
            ErrorCode.INVALID_CODE, "Status is the same as the current status");
      }
      if (!(status.equals(ReportStatus.IN_PROGRESS) || status.equals(ReportStatus.RESOLVED))) {
        throw new GlobalException(ErrorCode.INVALID_CODE, "Status must be IN_PROGRESS or RESOLVED");
      }
      if (status.equals(ReportStatus.IN_PROGRESS)) {
        report.setStatus(ReportStatus.IN_PROGRESS);
      } else {
        if (request.getSolution() == null || request.getSolution().isEmpty()) {
          throw new GlobalException(
              ErrorCode.INVALID_CODE, "Solution must be provided when status is RESOLVED");
        }
        report.setStatus(ReportStatus.RESOLVED);
        report.setSolution(request.getSolution());
      }
      Report savedReport = reportRepository.save(report);
      ReportResponse reportResponse = reportMapper.toReportResponse(savedReport);
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
