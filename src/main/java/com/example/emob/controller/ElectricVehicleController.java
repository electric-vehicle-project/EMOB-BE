package com.example.emob.controller;

import com.example.emob.entity.VehicleUnit;
import com.example.emob.model.request.ElectricVehicleRequest;
import com.example.emob.model.request.VehicleUnitRequest;
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

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/vehicle")
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
                    description = "Request payload for creating an electric vehicle",
                    required = true,
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ElectricVehicleRequest.class),
                            examples = {
                                    @ExampleObject(
                                            name = "Tesla Model S",
                                            value = """
                                        {
                                          "brand": "Tesla",
                                          "model": "Model S Plaid",
                                          "importPrice": 90000,
                                          "retailPrice": 120000,
                                          "batteryKwh": 100,
                                          "rangeKm": 650,
                                          "chargeTimeHr": 1.5,
                                          "powerKw": 450,
                                          "images": ["https://example.com/tesla-models-front.jpg", "https://example.com/tesla-models-side.jpg"],
                                          "weightKg": 2200,
                                          "topSpeedKmh": 322,
                                          "type": "CAR"
                                        }
                                        """
                                    ),
                                    @ExampleObject(
                                            name = "Yadea G5 Scooter",
                                            value = """
                                        {
                                          "brand": "Yadea",
                                          "model": "G5",
                                          "importPrice": 900,
                                          "retailPrice": 1500,
                                          "batteryKwh": 2.5,
                                          "rangeKm": 70,
                                          "chargeTimeHr": 3.5,
                                          "powerKw": 1.5,
                                          "images": ["https://example.com/yadea-g5-front.png", "https://example.com/yadea-g5-side.png"],
                                          "weightKg": 88,
                                          "topSpeedKmh": 65,
                                          "type": "SCOOTER"
                                        }
                                        """
                                    ),
                                    @ExampleObject(
                                            name = "Niu Electric Bike",
                                            value = """
                                        {
                                          "brand": "Niu",
                                          "model": "MQi+ Sport",
                                          "importPrice": 1200,
                                          "retailPrice": 1800,
                                          "batteryKwh": 1.6,
                                          "rangeKm": 80,
                                          "chargeTimeHr": 4,
                                          "powerKw": 1.0,
                                          "images": ["https://example.com/niu-mqi-sport.png"],
                                          "weightKg": 70,
                                          "topSpeedKmh": 45,
                                          "type": "BIKE"
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

    @PostMapping("/bulk")
    @Operation(
            summary = "Tạo hàng loạt xe Vehicle Units cho một mẫu xe cụ thể",
            description = "API này cho phép tạo nhiều xe (VehicleUnit) cùng lúc dựa trên mẫu xe (ElectricVehicle) có sẵn.",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    required = true,
                    description = "Thông tin yêu cầu tạo hàng loạt Vehicle Unit",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = VehicleUnitRequest.class),
                            examples = {
                                    @ExampleObject(
                                            name = "Create 10 Tesla Model 3 units",
                                            value = """
                                            {
                                              "vehicleId": "nhập fields vô đây",
                                              "quantity": 10,
                                              "color": "White",
                                              "purchaseDate": "2025-10-05T10:00:00",
                                              "warrantyStart": "2025-10-10",
                                              "warrantyEnd": "2028-10-10",
                                              "productionYear": "2025-01-01"
                                            }
                                            """
                                    )
                            }
                    )
            )
    )
    public ResponseEntity<APIResponse<List<VehicleUnit>>> createBulkVehicles(
            @RequestBody VehicleUnitRequest request
    ) {
        return ResponseEntity.ok(vehicleService.createBulkVehicles(request));
    }
}
