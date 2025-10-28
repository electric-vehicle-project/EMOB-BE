/* EMOB-2025 */
package com.example.emob.controller;

import com.example.emob.constant.InstallmentStatus;
import com.example.emob.model.request.installment.UpdateInstallmentRequest;
import com.example.emob.model.response.APIResponse;
import com.example.emob.model.response.InstallmentResponse;
import com.example.emob.model.response.PageResponse;
import com.example.emob.service.InstallmentPlanService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@CrossOrigin("*")
@RequestMapping("/api/installment")
@SecurityRequirement(name = "api")
@Tag(name = "Installment Plan Controller", description = "Endpoints for managing installment plans")
public class InstallmentPlanController {
  @Autowired InstallmentPlanService installmentPlanService;

  @PutMapping("/{id}")
  @Operation(
      summary = "Update Installment Plan",
      requestBody =
          @io.swagger.v3.oas.annotations.parameters.RequestBody(
              description = "Update Installment Plan after paid",
              required = true,
              content =
                  @Content(
                      mediaType = "application/json",
                      schema = @Schema(implementation = UpdateInstallmentRequest.class))))
  public ResponseEntity<APIResponse<InstallmentResponse>> updateInstallment(
      @PathVariable UUID id, @RequestBody @Valid UpdateInstallmentRequest request) {
    return ResponseEntity.ok(
        installmentPlanService.updateInstallmentByStatus(id, request.getStatus()));
  }

  @GetMapping("/{id}")
  @Operation(summary = "Get Installment Plan")
  public ResponseEntity<APIResponse<InstallmentResponse>> viewInstallment(
      @PathVariable("id") UUID id) {
    return ResponseEntity.ok(installmentPlanService.viewInstallmentPlan(id));
  }

  @GetMapping("/dealers")
  public APIResponse<PageResponse<InstallmentResponse>> getAllPlansOfDealers(
      @RequestParam(required = false) List<InstallmentStatus> statuses,
      @RequestParam(defaultValue = "0") int page,
      @RequestParam(defaultValue = "10") int size) {
    Pageable pageable = PageRequest.of(page, size);
    return installmentPlanService.getAllPlansOfDealers(statuses, pageable);
  }

  @GetMapping("/current-dealer")
  public APIResponse<PageResponse<InstallmentResponse>> getAllPlansOfCurrentDealer(
      @RequestParam(required = false) List<InstallmentStatus> statuses,
      @RequestParam(defaultValue = "0") int page,
      @RequestParam(defaultValue = "10") int size) {
    Pageable pageable = PageRequest.of(page, size);
    return installmentPlanService.getAllPlansOfCurrentDealer(statuses, pageable);
  }

  @GetMapping("/customer/{customerId}")
  public APIResponse<PageResponse<InstallmentResponse>> getAllPlansOfCurrentCustomer(
      @PathVariable UUID customerId,
      @RequestParam(required = false) List<InstallmentStatus> statuses,
      @RequestParam(defaultValue = "0") int page,
      @RequestParam(defaultValue = "10") int size) {
    Pageable pageable = PageRequest.of(page, size);
    return installmentPlanService.getAllPlansOfCurrentCustomer(customerId, statuses, pageable);
  }

  @GetMapping("/by-customer")
  public APIResponse<PageResponse<InstallmentResponse>> getAllPlansByCustomer(
      @RequestParam(required = false) List<InstallmentStatus> statuses,
      @RequestParam(defaultValue = "0") int page,
      @RequestParam(defaultValue = "10") int size) {
    Pageable pageable = PageRequest.of(page, size);
    return installmentPlanService.getAllPlansByCustomer(statuses, pageable);
  }
}
