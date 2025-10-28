/* EMOB-2025 */
package com.example.emob.controller;

import com.example.emob.constant.DeliveryStatus;
import com.example.emob.model.request.delivery.DeliveryRequest;
import com.example.emob.model.response.APIResponse;
import com.example.emob.model.response.DeliveryResponse;
import com.example.emob.model.response.PageResponse;
import com.example.emob.service.DeliveryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/delivery")
@CrossOrigin("*")
@Tag(name = "Delivery Controller", description = "Endpoints for deliver managing ")
@SecurityRequirement(name = "api")
public class DeliveryController {
  @Autowired DeliveryService deliveryService;

  @PostMapping("/dealer")
  public ResponseEntity<APIResponse<DeliveryResponse>> createDeliveryToDealer(
      @Valid @RequestBody DeliveryRequest request) {
    return ResponseEntity.ok(deliveryService.createDeliveryToDealer(request));
  }

  @PostMapping("/customer")
  public ResponseEntity<APIResponse<DeliveryResponse>> createDeliveryToCustomer(
      @Valid @RequestBody DeliveryRequest request) {
    return ResponseEntity.ok(deliveryService.createDeliveryToCustomer(request));
  }

  @DeleteMapping("/{id}")
  @Operation(summary = "Delete Deliver")
  public ResponseEntity<APIResponse<Void>> deleteDelivery(
      @RequestParam @PathVariable("id") UUID id) {
    return ResponseEntity.ok(deliveryService.deleteDelivery(id));
  }
  private Pageable buildPageable(int page, int size, String[] sort) {
    Sort sortObj = Sort.by(Arrays.stream(sort)
            .map(s -> {
              String[] _s = s.split(",");
              return new Sort.Order(
                      _s.length > 1 && _s[1].equalsIgnoreCase("desc") ? Sort.Direction.DESC : Sort.Direction.ASC,
                      _s[0]
              );
            })
            .toList());
    return PageRequest.of(page, size, sortObj);
  }

  @GetMapping("/dealers")
  public APIResponse<PageResponse<DeliveryResponse>> getAllDeliveriesOfDealers(
          @RequestParam(required = false) List<DeliveryStatus> statuses,
          @RequestParam(defaultValue = "0") int page,
          @RequestParam(defaultValue = "10") int size,
          @RequestParam(defaultValue = "id,desc") String[] sort) {

    Pageable pageable = buildPageable(page, size, sort);
    return deliveryService.getAllDeliveriesOfDealers(statuses, pageable);
  }

  @GetMapping("/dealer/current")
  public APIResponse<PageResponse<DeliveryResponse>> getAllDeliveriesOfCurrentDealer(
          @RequestParam(required = false) List<DeliveryStatus> statuses,
          @RequestParam(defaultValue = "0") int page,
          @RequestParam(defaultValue = "10") int size,
          @RequestParam(defaultValue = "id,desc") String[] sort) {

    Pageable pageable = buildPageable(page, size, sort);
    return deliveryService.getAllDeliveriesOfCurrentDealer(statuses, pageable);
  }

  @GetMapping("/customers")
  public APIResponse<PageResponse<DeliveryResponse>> getAllDeliveriesByCustomer(
          @RequestParam(required = false) List<DeliveryStatus> statuses,
          @RequestParam(defaultValue = "0") int page,
          @RequestParam(defaultValue = "10") int size,
          @RequestParam(defaultValue = "id,desc") String[] sort) {

    Pageable pageable = buildPageable(page, size, sort);
    return deliveryService.getAllDeliveriesByCustomer(statuses, pageable);
  }

  @GetMapping("/customer/{customerId}")
  public APIResponse<PageResponse<DeliveryResponse>> getAllDeliveriesOfCurrentCustomer(
          @PathVariable UUID customerId,
          @RequestParam(required = false) List<DeliveryStatus> statuses,
          @RequestParam(defaultValue = "0") int page,
          @RequestParam(defaultValue = "10") int size,
          @RequestParam(defaultValue = "id,desc") String[] sort) {

    Pageable pageable = buildPageable(page, size, sort);
    return deliveryService.getAllDeliveriesOfCurrentCustomer(customerId, statuses, pageable);
  }

  @GetMapping("/{deliveryId}")
  public APIResponse<DeliveryResponse> getDeliveryById(@PathVariable UUID deliveryId) {
    return deliveryService.getDeliveryById(deliveryId);
  }

  @PutMapping("/{deliveryId}/complete")
  public APIResponse<DeliveryResponse> completeDelivery(@PathVariable UUID deliveryId) {
    return deliveryService.completeDelivery(deliveryId);
  }
}
