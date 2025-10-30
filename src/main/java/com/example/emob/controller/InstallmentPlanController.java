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
import org.springframework.data.domain.Sort;
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
  public ResponseEntity<APIResponse<PageResponse<InstallmentResponse>>> getAllPlansOfDealers(
      @RequestParam(defaultValue = "0") int page,
      @RequestParam(defaultValue = "10") int size,
      @RequestParam(required = false) String keyword,
      @RequestParam(required = false) List<InstallmentStatus> statuses,
      @RequestParam(defaultValue = "createdAt") String sortField,
      @RequestParam(defaultValue = "desc") String sortDir) {

    Sort sort = Sort.by(sortField);
    sort = "asc".equalsIgnoreCase(sortDir) ? sort.ascending() : sort.descending();
    Pageable pageable = PageRequest.of(page, size, sort);

    APIResponse<PageResponse<InstallmentResponse>> response =
        installmentPlanService.getAllPlansOfDealers(statuses, pageable, keyword);

    return ResponseEntity.ok(response);
  }

  @GetMapping("/current-dealer")
  public APIResponse<PageResponse<InstallmentResponse>> getAllPlansOfCurrentDealer(
      @RequestParam(defaultValue = "0") int page,
      @RequestParam(defaultValue = "10") int size,
      @RequestParam(required = false) String keyword,
      @RequestParam(required = false) List<InstallmentStatus> statuses,
      @RequestParam(defaultValue = "createdAt") String sortField,
      @RequestParam(defaultValue = "desc") String sortDir) {

    Sort sort = Sort.by(sortField);
    sort = "asc".equalsIgnoreCase(sortDir) ? sort.ascending() : sort.descending();
    Pageable pageable = PageRequest.of(page, size, sort);
    return installmentPlanService.getAllPlansOfCurrentDealer(statuses, pageable, keyword);
  }

  @GetMapping("/customer/{customerId}")
  public APIResponse<PageResponse<InstallmentResponse>> getAllPlansOfCurrentCustomer(
      @PathVariable UUID customerId,
      @RequestParam(defaultValue = "0") int page,
      @RequestParam(defaultValue = "10") int size,
      @RequestParam(required = false) String keyword,
      @RequestParam(required = false) List<InstallmentStatus> statuses,
      @RequestParam(defaultValue = "createdAt") String sortField,
      @RequestParam(defaultValue = "desc") String sortDir) {

    Sort sort = Sort.by(sortField);
    sort = "asc".equalsIgnoreCase(sortDir) ? sort.ascending() : sort.descending();
    Pageable pageable = PageRequest.of(page, size, sort);

    return installmentPlanService.getAllPlansOfCurrentCustomer(
        customerId, statuses, pageable, keyword);
  }

  @GetMapping("/by-customer")
  public APIResponse<PageResponse<InstallmentResponse>> getAllPlansByCustomer(
      @RequestParam(defaultValue = "0") int page,
      @RequestParam(defaultValue = "10") int size,
      @RequestParam(required = false) String keyword,
      @RequestParam(required = false) List<InstallmentStatus> statuses,
      @RequestParam(defaultValue = "createdAt") String sortField,
      @RequestParam(defaultValue = "desc") String sortDir) {

    Sort sort = Sort.by(sortField);
    sort = "asc".equalsIgnoreCase(sortDir) ? sort.ascending() : sort.descending();
    Pageable pageable = PageRequest.of(page, size, sort);
    return installmentPlanService.getAllPlansByCustomer(statuses, pageable, keyword);
  }
}
