package com.example.emob.controller;

import com.example.emob.model.request.ElectricVehicleRequest;
import com.example.emob.model.response.APIResponse;
import com.example.emob.model.response.ElectricVehicleResponse;
import com.example.emob.model.response.PageResponse;
import com.example.emob.service.ElectricVehicleService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/evm-staff/vehicle")
@CrossOrigin("*")
@Tag(name = "Electric Vehicle Controller", description = "Endpoints for managing electric vehicles")
@SecurityRequirement(name = "api")
@Slf4j
public class ElectricVehicleController {

    @Autowired
    private ElectricVehicleService vehicleService;

    @PostMapping
    @Operation(
            summary = "Create a new Electric Vehicle",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Electric vehicle creation request",
                    required = true,
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ElectricVehicleRequest.class),
                            examples = {
                                    @ExampleObject(
                                            name = "Tesla Model 3",
                                            value = """
                                            {
                                              "brand": "Tesla",
                                              "model": "Model 3",
                                              "importPrice": 30000,
                                              "retailPrice": 40000,
                                              "batteryKwh": 75,
                                              "rangeKm": 500,
                                              "chargeTimeHr": 6.5,
                                              "powerKw": 250,
                                              "images": ["https://example.com/model3-front.jpg"],
                                              "weightKg": 1800,
                                              "topSpeedKmh": 225,
                                              "type": "CAR"
                                            }
                                            """
                                    ),
                                    @ExampleObject(
                                            name = "Yadea G5",
                                            value = """
                                            {
                                              "brand": "Yadea",
                                              "model": "G5",
                                              "importPrice": 800,
                                              "retailPrice": 1200,
                                              "batteryKwh": 2.3,
                                              "rangeKm": 60,
                                              "chargeTimeHr": 4,
                                              "powerKw": 1.2,
                                              "images": ["https://example.com/yadea-g5.png"],
                                              "weightKg": 85,
                                              "topSpeedKmh": 60,
                                              "type": "SCOOTER"
                                            }
                                            """
                                    )
                            }
                    )
            )
    )
    public ResponseEntity<APIResponse<ElectricVehicleResponse>> createVehicle(
            @Valid @RequestBody ElectricVehicleRequest request
    ) {
        return ResponseEntity.ok(vehicleService.create(request));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get electric vehicle by ID")
    public ResponseEntity<APIResponse<ElectricVehicleResponse>> getVehicle(@PathVariable UUID id) {
        return ResponseEntity.ok(vehicleService.get(id));
    }

    @GetMapping
    @Operation(summary = "Get all electric vehicles")
    public ResponseEntity<APIResponse<PageResponse<ElectricVehicleResponse>>> getAllVehicles(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        Pageable pageable = PageRequest.of(page, size);
        return ResponseEntity.ok(vehicleService.getAll(pageable));
    }

    @PutMapping("/{id}")
    @Operation(
            summary = "Update electric vehicle by ID",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Electric vehicle update request",
                    required = true,
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ElectricVehicleRequest.class)
                    )
            )
    )
    public ResponseEntity<APIResponse<ElectricVehicleResponse>> updateVehicle(
            @PathVariable UUID id,
            @Valid @RequestBody ElectricVehicleRequest request
    ) {
        return ResponseEntity.ok(vehicleService.update(id, request));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete electric vehicle by ID")
    public ResponseEntity<APIResponse<ElectricVehicleResponse>> deleteVehicle(@PathVariable UUID id) {
        return ResponseEntity.ok(vehicleService.delete(id));
    }
}
