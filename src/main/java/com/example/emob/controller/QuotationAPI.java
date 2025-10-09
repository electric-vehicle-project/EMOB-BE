/* EMOB-2025 */
package com.example.emob.controller;

import com.example.emob.service.QuotationService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/quotation")
@CrossOrigin("*")
@SecurityRequirement(name = "api")
public class QuotationAPI {

  @Autowired private QuotationService quotationService;
}
