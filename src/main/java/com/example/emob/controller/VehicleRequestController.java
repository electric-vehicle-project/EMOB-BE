/* EMOB-2025 */
package com.example.emob.controller;

import com.example.emob.constant.PaymentStatus;
import com.example.emob.model.request.vehicleRequest.VehicleRequestItemRequest;
import com.example.emob.model.request.vehicleRequest.VehicleRequestItemUpdateRequest;
import com.example.emob.model.request.vehicleRequest.VehicleRequestRequest;
import com.example.emob.model.response.APIResponse;
import com.example.emob.model.response.PageResponse;
import com.example.emob.model.response.vehicleRequest.VehicleRequestResponse;
import com.example.emob.service.VehicleRequestService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/vehicle-request")
@CrossOrigin("*")
@SecurityRequirement(name = "api")
public class VehicleRequestController {

  @Autowired private VehicleRequestService vehiclerequestService;

  @PostMapping
  public ResponseEntity<APIResponse<VehicleRequestResponse>> create(
      @RequestBody VehicleRequestRequest<VehicleRequestItemRequest> request) {
    return ResponseEntity.ok(vehiclerequestService.create(request));
  }

  @Operation(summary = "Update a vehicle request")
  @PutMapping("/{id}")
  public APIResponse<VehicleRequestResponse> update(
      @PathVariable UUID id,
      @RequestBody VehicleRequestRequest<VehicleRequestItemUpdateRequest> request) {
    return vehiclerequestService.update(id, request);
  }

  @PutMapping("{id}/approved")
  public ResponseEntity<APIResponse<VehicleRequestResponse>> approved(
      @RequestParam UUID id, @RequestBody PaymentStatus paymentStatus) {
    return ResponseEntity.ok(vehiclerequestService.approveVehicleRequest(id, paymentStatus));
  }

  // 🔹 Delete Vehicle Request (soft delete)
  @Operation(summary = "Soft delete a vehicle request")
  @DeleteMapping("/{id}")
  public APIResponse<VehicleRequestResponse> delete(@PathVariable UUID id) {
    return vehiclerequestService.delete(id);
  }

  // 🔹 Get Vehicle Request by ID
  @Operation(summary = "Get vehicle request by ID")
  @GetMapping("/{id}")
  public APIResponse<VehicleRequestResponse> get(@PathVariable UUID id) {
    return vehiclerequestService.get(id);
  }

  // 🔹 Get All Vehicle Requests (pagination)
  @Operation(summary = "Get all vehicle requests")
  @GetMapping
  public APIResponse<PageResponse<VehicleRequestResponse>> getAll(
      @RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "10") int size) {
    Pageable pageable = PageRequest.of(page, size);
    return vehiclerequestService.getAll(pageable);
  }
}
