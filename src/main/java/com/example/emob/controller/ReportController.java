package com.example.emob.controller;

import com.example.emob.model.request.report.CreateReportRequest;
import com.example.emob.model.request.report.UpdateReportRequest;
import com.example.emob.model.response.APIResponse;
import com.example.emob.model.response.ReportResponse;
import com.example.emob.service.ReportService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
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
    public ResponseEntity<APIResponse<ReportResponse>> generateReport(@RequestBody @Valid CreateReportRequest request) {
        return ResponseEntity.ok(reportService.createReport(request));
    }

    @PutMapping("/update/{reportId}")
    public ResponseEntity<APIResponse<ReportResponse>> updateReport(@RequestBody @Valid UpdateReportRequest request, @PathVariable("reportId") UUID reportId) {
        return ResponseEntity.ok(reportService.updateReport(request, reportId));
    }

    @DeleteMapping("/delete/{reportId}")
    public ResponseEntity<APIResponse<ReportResponse>> deleteReport(@PathVariable("reportId") UUID reportId) {
        return ResponseEntity.ok(reportService.deleteReport(reportId));
    }

    @GetMapping("/view/{title}/{reportId}")
    public ResponseEntity<APIResponse<ReportResponse>> viewReport(@PathVariable("reportId") UUID reportId) {
        return ResponseEntity.ok(reportService.viewReport(reportId));
    }
}
