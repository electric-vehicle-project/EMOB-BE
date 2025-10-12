package com.example.emob.controller;

import com.example.emob.model.request.delivery.DeliveryItemRequest;
import com.example.emob.model.response.APIResponse;
import com.example.emob.model.response.DeliveryItemResponse;
import com.example.emob.model.response.PageResponse;
import com.example.emob.service.DeliveryItemService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/delivery-item")
@CrossOrigin("*")
@Tag(name = "Delivery Item Controller", description = "Endpoints for deliver item managing ")
@SecurityRequirement(name = "api")
public class DeliveryItemController {
    @Autowired
    DeliveryItemService deliveryItemService;

//     @PostMapping
//     @Operation(
//             summary = "Create Delivery Item",
//             requestBody =
//             @io.swagger.v3.oas.annotations.parameters.RequestBody(
//                     description = "create item delivery",
//                     required = true,
//                     content =
//                     @Content(
//                             mediaType = "application/json",
//                             schema = @Schema(implementation = DeliveryItemRequest.class)
//                     )
//             )
//     )
//     public ResponseEntity<APIResponse<DeliveryItemResponse>> create (@Valid @RequestBody DeliveryItemRequest request) {
//         return ResponseEntity.ok(deliveryItemService.createDeliveryItem(request));
//     }

//     @PostMapping("/{id}")
//     @Operation(
//             summary = "Confirm Delivery Item"
//     )
//     public ResponseEntity<APIResponse<DeliveryItemResponse>> createDelivery (@RequestParam @PathVariable("id") UUID id) {
//         return ResponseEntity.ok(deliveryItemService.confirm(id));
//     }

//     @GetMapping("/{id}")
//     @Operation(
//             summary = "View Delivery Item"
//     )
//     public ResponseEntity<APIResponse<DeliveryItemResponse>> view (@RequestParam @PathVariable("id") UUID id) {
//         return ResponseEntity.ok(deliveryItemService.viewDeliveryItem(id));
//     }

//     @GetMapping("/{id}")
//     @Operation(
//             summary = "View Delivery Item"
//     )
//     public ResponseEntity<APIResponse<PageResponse<DeliveryItemResponse>>> viewAll (
//             @RequestParam(defaultValue = "0") int page,
//             @RequestParam(defaultValue = "10") int size
//     ) {
//         Pageable pageable = PageRequest.of(page, size);
//         return ResponseEntity.ok(deliveryItemService.viewDeliveriesItem(pageable));
//     }

//     @DeleteMapping("/{id}")
//     @Operation(
//             summary = "Cancel Delivery Item"
//     )
//     public ResponseEntity<APIResponse<DeliveryItemResponse>> cancelDeliveryItem (@RequestParam @PathVariable("id") UUID id) {
//         return ResponseEntity.ok(deliveryItemService.cancelDeliveryItem(id));
//     }
}
