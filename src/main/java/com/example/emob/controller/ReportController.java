/* EMOB-2025 */
package com.example.emob.controller;

import com.example.emob.constant.ReportStatus;
import com.example.emob.model.request.report.CreateReportRequest;
import com.example.emob.model.request.report.ResolveReportRequest;
import com.example.emob.model.request.report.UpdateReportRequest;
import com.example.emob.model.response.APIResponse;
import com.example.emob.model.response.PageResponse;
import com.example.emob.model.response.ReportResponse;
import com.example.emob.service.ReportService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@CrossOrigin("*")
@RequestMapping("/api/report")
@SecurityRequirement(name = "api")
@Tag(name = "Report Controller", description = "Endpoints for managing reports")
public class ReportController {
  @Autowired ReportService reportService;

  @PostMapping
  @Operation(
      summary = "Generate Report",
      requestBody =
          @io.swagger.v3.oas.annotations.parameters.RequestBody(
              description = "Generate Report",
              required = true,
              content =
                  @Content(
                      mediaType = "application/json",
                      schema = @Schema(implementation = CreateReportRequest.class),
                      examples = {
                        @ExampleObject(
                            name = "Feedback Report",
                            value =
                                """
                                                                   {
                                                         "customerId": "29e59bdf-9dcd-11f0-ac59-0242ac110002",
                                                        "description": "string",
                                                        "title": "string",
                                                        "status": "PENDING",
                                                        "type": "FEEDBACK"

                                                    }


                                                    """),
                        @ExampleObject(
                            name = "Complaint Report",
                            value =
                                """
                                                                   {
                                                                     "customerId": "29e59bdf-9dcd-11f0-ac59-0242ac110002",
                                                                     "description": "bad",
                                                                     "title": "giá cả",
                                                                     "status": "PENDING",
                                                                     "type": "COMPLAINT"
                                                                   }


                                                    """)
                      })))
  public ResponseEntity<APIResponse<ReportResponse>> generateReport(
      @RequestBody @Valid CreateReportRequest request) {
    return ResponseEntity.ok(reportService.createReport(request));
  }

  @PutMapping("/{reportId}")
  @Operation(
      summary = "Update Report",
      requestBody =
          @io.swagger.v3.oas.annotations.parameters.RequestBody(
              description = "Update Report",
              required = true,
              content =
                  @Content(
                      mediaType = "application/json",
                      schema = @Schema(implementation = UpdateReportRequest.class),
                      examples = {
                        @ExampleObject(
                            name = "Update Field",
                            value =
                                """
                                                                   {
                                                                     "title": "thanh toán",
                                                                     "description": "fail",
                                                                     "status": "PENDING",
                                                                     "type": "FEEDBACK"
                                                                   }


                                                    """)
                      })))
  public ResponseEntity<APIResponse<ReportResponse>> updateReport(
      @RequestBody @Valid UpdateReportRequest request, @PathVariable("reportId") UUID reportId) {
    return ResponseEntity.ok(reportService.updateReport(request, reportId));
  }

  @DeleteMapping("/{reportId}")
  @Operation(summary = "Delete Report")
  public ResponseEntity<APIResponse<ReportResponse>> deleteReport(
      @PathVariable("reportId") UUID reportId) {
    return ResponseEntity.ok(reportService.deleteReport(reportId));
  }

  @GetMapping("/{reportId}")
  @Operation(summary = "View Report")
  public ResponseEntity<APIResponse<ReportResponse>> viewReport(
      @PathVariable("reportId") UUID reportId) {
    return ResponseEntity.ok(reportService.viewReport(reportId));
  }

  @GetMapping("/view-all")
  @Operation(summary = "View All Reports")
  public ResponseEntity<APIResponse<PageResponse<ReportResponse>>> viewAllReports(
      @RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "10") int size) {
    Pageable pageable = PageRequest.of(page, size);
    return ResponseEntity.ok(reportService.viewAllReport(pageable));
  }

  @PutMapping("/process-report/{reportId}")
  @Operation(summary = "Change Status Report")
  public ResponseEntity<APIResponse<ReportResponse>> changeStatus(
      @PathVariable("reportId") UUID reportId,
      @RequestParam() ReportStatus status,
      @RequestBody ResolveReportRequest request) {
    return ResponseEntity.ok(reportService.changeStatus(reportId, status, request));
  }
}
