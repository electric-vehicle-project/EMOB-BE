package com.example.emob.controller;

import com.example.emob.model.request.DealerRequest;
import com.example.emob.model.response.APIResponse;
import com.example.emob.model.response.DealerResponse;
import com.example.emob.model.response.PageResponse;
import com.example.emob.service.DealerService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/admin/dealer")
@CrossOrigin("*")
@Tag(name = "Dealer Controller", description = "Endpoints for managing dealers")
@SecurityRequirement(name = "api")
public class DealerController {

    @Autowired
    private DealerService dealerService;

    @PostMapping
    @Operation(
            summary = "Create a new Dealer",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Dealer creation request",
                    required = true,
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = DealerRequest.class),
                            examples = {
                                    @ExampleObject(
                                            name = "Dealer A",
                                            value = """
                            {
                              "name": "Dealer One",
                              "contactInfo": "dealer1@example.com",
                              "country": "USA"
                            }
                            """
                                    ),
                                    @ExampleObject(
                                            name = "Dealer B",
                                            value = """
                            {
                              "name": "Dealer Two",
                              "contactInfo": "dealer2@example.com",
                              "country": "Canada"
                            }
                            """
                                    )
                            }
                    )
            )
    )
    public ResponseEntity<APIResponse<DealerResponse>> createDealer(@Valid @RequestBody DealerRequest request) {
        return ResponseEntity.ok(dealerService.create(request));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get dealer by ID")
    public ResponseEntity<APIResponse<DealerResponse>> getDealer(@PathVariable UUID id) {
        return ResponseEntity.ok(dealerService.get(id));
    }
    @GetMapping
    @Operation(summary = "Get all dealers")
    public ResponseEntity<APIResponse<PageResponse<DealerResponse>>> getAllDealers(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        Pageable pageable = PageRequest.of(page, size);
        return ResponseEntity.ok(dealerService.getAll(pageable));
    }

    @PutMapping("/{id}")
    @Operation(
            summary = "Update dealer by ID",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Dealer update request",
                    required = true,
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = DealerRequest.class)
                    )
            )
    )
    public ResponseEntity<APIResponse<DealerResponse>> updateDealer(
            @PathVariable UUID id,
            @Valid @RequestBody DealerRequest request
    ) {
        return ResponseEntity.ok(dealerService.update(id, request));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete dealer by ID")
    public ResponseEntity<APIResponse<DealerResponse>> deleteDealer(@PathVariable UUID id) {
        dealerService.delete(id);
        return ResponseEntity.ok(dealerService.delete(id));
    }
}
