/* EMOB-2025 */
package com.example.emob.controller;

import com.example.emob.model.request.quotation.QuotationRequest;
import com.example.emob.model.response.APIResponse;
import com.example.emob.model.response.quotation.QuotationResponse;
import com.example.emob.service.QuotationService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/quotation")
@CrossOrigin("*")
@SecurityRequirement(name = "api")
public class QuotationController {

  @Autowired private QuotationService quotationService;

  @PostMapping
  public ResponseEntity<APIResponse<QuotationResponse>> create(
      @RequestBody QuotationRequest quotationRequest) {
    return ResponseEntity.ok(quotationService.create(quotationRequest));
  }
}
