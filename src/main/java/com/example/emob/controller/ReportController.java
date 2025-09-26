package com.example.emob.controller;

import com.example.emob.model.request.ReportRequest;
import com.example.emob.model.response.APIResponse;
import com.example.emob.model.response.ReportResponse;
import com.example.emob.service.ReportService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@CrossOrigin("*")
@RequestMapping("/api/reports")
@SecurityRequirement(name = "api")
public class ReportController {
    @Autowired
    ReportService reportService;
    @PostMapping("/generate")
    public ResponseEntity<APIResponse<ReportResponse>> generateReport(@RequestBody @Valid ReportRequest request) {
        return ResponseEntity.ok(reportService.createReport(request));
    }
}
