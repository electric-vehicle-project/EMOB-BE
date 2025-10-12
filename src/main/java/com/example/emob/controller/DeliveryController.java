package com.example.emob.controller;

import com.example.emob.model.request.delivery.DeliveryRequest;
import com.example.emob.model.request.delivery.UpdateDeliveryRequest;
import com.example.emob.model.response.APIResponse;
import com.example.emob.model.response.DeliveryResponse;
import com.example.emob.model.response.PageResponse;
import com.example.emob.service.DeliveryService;
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
@RequestMapping("/api/delivery")
@CrossOrigin("*")
@Tag(name = "Delivery Controller", description = "Endpoints for deliver managing ")
@SecurityRequirement(name = "api")
public class DeliveryController {
    @Autowired
    DeliveryService deliveryService;

//     @PostMapping
//     @Operation(
//             summary = "Create Delivery",
//             requestBody =
//             @io.swagger.v3.oas.annotations.parameters.RequestBody(
//                     description = "create delivery",
//                     required = true,
//                     content =
//                     @Content(
//                             mediaType = "application/json",
//                             schema = @Schema(implementation = DeliveryRequest.class)
//                     )
//             )
//     )
//     public ResponseEntity<APIResponse<DeliveryResponse>> createDelivery (@Valid @RequestBody DeliveryRequest request) {
//         return ResponseEntity.ok(deliveryService.createDelivery(request));
//     }

//     @PutMapping("/{id}")
//     @Operation(
//             summary = "Update Delivery",
//             requestBody =
//             @io.swagger.v3.oas.annotations.parameters.RequestBody(
//                     description = "update delivery date",
//                     required = true,
//                     content =
//                     @Content(
//                             mediaType = "application/json",
//                             schema = @Schema(implementation = UpdateDeliveryRequest.class)
//                     )
//             )
//     )
//     public ResponseEntity<APIResponse<DeliveryResponse>> updateDelivery (@Valid @RequestBody UpdateDeliveryRequest request,
//                                                                         @RequestParam @PathVariable("id") UUID id) {
//         return ResponseEntity.ok(deliveryService.updateDeliveryDate(request, id));
//     }

//     @GetMapping("/{id}")
//     @Operation(
//             summary = "View Delivery"
//     )
//     public ResponseEntity<APIResponse<DeliveryResponse>> viewDelivery (@RequestParam @PathVariable("id") UUID id) {
//         return ResponseEntity.ok(deliveryService.getDelivery(id));
//     }

//     @GetMapping("/view-all")
//     @Operation(
//             summary = "View All Delivers"
//     )
//     public ResponseEntity<APIResponse<PageResponse<DeliveryResponse>>> viewAllDelivers (
//                                 @RequestParam(defaultValue = "0") int page,
//                                 @RequestParam(defaultValue = "10") int size
//     ) {
//         Pageable pageable = PageRequest.of(page, size);
//         return ResponseEntity.ok(deliveryService.getAllDeliveries(pageable));
//     }

//     @DeleteMapping("/{id}")
//     @Operation(
//             summary = "Delete Deliver"
//     )
//     public ResponseEntity<APIResponse<Void>> viewAllDelivers (@RequestParam @PathVariable("id") UUID id) {
//         return ResponseEntity.ok(deliveryService.deleteDelivery(id));
//     }

}
