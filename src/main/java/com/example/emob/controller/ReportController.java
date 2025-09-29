package com.example.emob.controller;

import com.example.emob.constant.ReportStatus;
import com.example.emob.model.request.report.CreateReportRequest;
import com.example.emob.model.request.report.UpdateReportRequest;
import com.example.emob.model.response.APIResponse;
import com.example.emob.model.response.ReportResponse;
import com.example.emob.service.ReportService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@CrossOrigin("*")
@RequestMapping("/api/reports")
@SecurityRequirement(name = "api")
@Tag(name = "Report", description = "CRUD API for Report")
public class ReportController {
    @Autowired
    ReportService reportService;

    @PostMapping("/generate")
    @Operation(
            summary = "Generate Report",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Generate Report",
                    required = true,
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = CreateReportRequest.class),
                            examples = {
                                    @ExampleObject(
                                            name = "Feedback Report",
                                            value = """
                                                                   {
                                                      "customerId": "79b7f9b8-9d3d-11f0-b102-0242ac110002",
                                                      "description": "good",
                                                      "title": "nhận định",
                                                      "status": "PENDING",
                                                      "type": "FEEDBACK"
                                                    }
                                                    
                                                    
                                                    """
                                    ),
                                    @ExampleObject(
                                            name = "Complaint Report",
                                            value = """
                                                                   {
                                                                     "customerId": "79b7f9b8-9d3d-11f0-b102-0242ac110002",
                                                                     "description": "bad",
                                                                     "title": "giá cả",
                                                                     "status": "PENDING",
                                                                     "type": "COMPLAINT"
                                                                   }
                                                    
                                                    
                                                    """
                                    )
                            }
                    )
            )
    )

    public ResponseEntity<APIResponse<ReportResponse>> generateReport(@RequestBody @Valid CreateReportRequest request) {
        return ResponseEntity.ok(reportService.createReport(request));
    }


    @PutMapping("/update/{reportId}")
    @Operation(
            summary = "Update Report",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Update Report",
                    required = true,
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = UpdateReportRequest.class),
                            examples = {
                                    @ExampleObject(
                                            name = "Update Field",
                                            value = """
                                                                   {
                                                                     "title": "thanh toán",
                                                                     "description": "fail",
                                                                     "status": "PENDING",
                                                                     "type": "FEEDBACK"
                                                                   }
                                                    
                                                    
                                                    """
                                    )
                            }
                    )
            )
    )
    public ResponseEntity<APIResponse<ReportResponse>> updateReport(@RequestBody @Valid UpdateReportRequest request, @PathVariable("reportId") UUID reportId) {
        return ResponseEntity.ok(reportService.updateReport(request, reportId));
    }

    @DeleteMapping("/delete/{reportId}")
    @Operation(
            summary = "Delete Report",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Update status of Report by deleted",
                    required = true,
                    content = @Content(
                            mediaType = "application/json",
                            examples = {
                                    @ExampleObject(
                                            name = "Delete Report",
                                            description = "Set status of report",
                                            value = """
                                                    b9bf95d6-e51d-4fd2-8980-55445edf5356
                                                    
                                                    
                                                    """
                                    )
                            }
                    )
            )
    )
    public ResponseEntity<APIResponse<ReportResponse>> deleteReport(@PathVariable("reportId") UUID reportId) {
        return ResponseEntity.ok(reportService.deleteReport(reportId));
    }

    @GetMapping("/view/{reportId}")
    @Operation(
            summary = "View Report",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "View Report",
                    required = true,
                    content = @Content(
                            mediaType = "application/json",
                            examples = {
                                    @ExampleObject(
                                            name = "View Report",
                                            description = "View report by ID",
                                            value = """
                                                    b9bf95d6-e51d-4fd2-8980-55445edf5356
                                                    
                                                    
                                                    """
                                    )
                            }
                    )
            )
    )
    public ResponseEntity<APIResponse<ReportResponse>> viewReport(@PathVariable("reportId") UUID reportId) {
        return ResponseEntity.ok(reportService.viewReport(reportId));
    }

    @GetMapping("/view-all")
    @Operation(
            summary = "View All Report",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "View All Report",
                    required = true,
                    content = @Content(
                            mediaType = "application/json",
                            examples = {
                                    @ExampleObject(
                                            name = "View All Report",
                                            description = "View All Report By Title",
                                            value = """
                                                    {
                                                      "page": 0,
                                                      "size": 12,
                                                      "sort": [
                                                        "title"
                                                      ]
                                                    }
                                                    
                                                    
                                                    """
                                    )
                            }
                    )
            )
    )
    public ResponseEntity<APIResponse<Page<ReportResponse>>> viewAllReports(Pageable pageable) {
        return ResponseEntity.ok(reportService.viewAllReport(pageable));
    }

    @PutMapping("/change-status/{reportId}")
    @Operation(
            summary = "Change Status Report",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Process Report",
                    required = true,
                    content = @Content(
                            mediaType = "application/json",
                            examples = {
                                    @ExampleObject(
                                            name = "Process Report",
                                            description = "Change status of report by IN_PROGRESS or RESOLVED",
                                            value = """
                                                    {
                                                          "reportId": "b9bf95d6-e51d-4fd2-8980-55445edf5356",
                                                          "status": "RESOLVED"
                                                        }
                                                    """
                                    )
                            }
                    )
            )
    )
    public ResponseEntity<APIResponse<ReportResponse>> changeStatus(@PathVariable("reportId") UUID reportId, ReportStatus status) {
        return ResponseEntity.ok(reportService.changeStatus(reportId, status));
    }
}
