package com.example.emob.controller;

import com.example.emob.model.request.report.CreateReportRequest;
import com.example.emob.model.response.APIResponse;
import com.example.emob.model.response.ReportResponse;
import com.example.emob.service.ReportService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
}
