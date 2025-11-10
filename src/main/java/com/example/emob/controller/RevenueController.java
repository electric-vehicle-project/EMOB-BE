package com.example.emob.controller;

import com.example.emob.constant.Region;
import com.example.emob.model.response.APIResponse;
import com.example.emob.model.response.MonthlyDealerRevenueResponse;
import com.example.emob.service.DealerService;
import com.example.emob.service.RevenueService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@CrossOrigin("*")
@RequestMapping("/api/revenue")
@SecurityRequirement(name = "api")
public class RevenueController {
    @Autowired
    private RevenueService revenueService;

    @GetMapping("/dealers-revenue")
    @Operation(summary = "Get dealers revenue report")
    public ResponseEntity<APIResponse<List<MonthlyDealerRevenueResponse>>> getDealerRevenueReport(
            @RequestParam(required = false) Region region,
            @RequestParam(required = false) Integer year,
            @RequestParam(required = false) String country) {

        // Nếu year không truyền, lấy năm hiện tại
        if (year == null) {
            year = LocalDate.now().getYear();
        }

        APIResponse<List<MonthlyDealerRevenueResponse>> data = revenueService.getDealerRevenueReport(year, region, country);
        return ResponseEntity.ok(data);
    }


    @GetMapping("/current-dealer-revenue")
    @Operation(summary = "Get  revenue report of dealer")
    public ResponseEntity<APIResponse<List<MonthlyDealerRevenueResponse>>> getDealerRevenueReportOfDealer(
            @RequestParam(required = false) Integer year) {
        if (year == null) {
            year = LocalDate.now().getYear();
        }
        return ResponseEntity.ok(revenueService.getDealerRevenueReportOfDealer(year));
    }
}
