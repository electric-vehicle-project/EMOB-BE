/* EMOB-2025 */
package com.example.emob.controller;

import com.example.emob.constant.DiscountPolicyStatus;
import com.example.emob.model.request.dealerDiscountPolicy.DealerDiscountPolicyBulkDeleteRequest;
import com.example.emob.model.request.dealerDiscountPolicy.DealerDiscountPolicyBulkRequest;
import com.example.emob.model.request.dealerDiscountPolicy.DealerDiscountPolicyRequest;
import com.example.emob.model.response.APIResponse;
import com.example.emob.model.response.DealerDiscountPolicyResponse;
import com.example.emob.model.response.PageResponse;
import com.example.emob.service.DealerDiscountPolicyService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
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
@RequestMapping("/api/dealer-discount-policy")
@CrossOrigin("*")
@Tag(name = "Dealer discount policy Controller", description = "Endpoints for managing dealers")
@SecurityRequirement(name = "api")
public class DealerDiscountPolicyController {
  @Autowired private DealerDiscountPolicyService dealerDiscountPolicyService;

  // ================= BULK CREATE =================
  @PostMapping("/bulk-create")
  @Operation(
      summary = "Bulk create Dealer Discount Policies",
      description = "Create new DealerDiscountPolicies for multiple dealers and vehicles",
      requestBody =
          @io.swagger.v3.oas.annotations.parameters.RequestBody(
              description = "DealerDiscountPolicyBulkRequest payload",
              required = true,
              content =
                  @Content(
                      mediaType = "application/json",
                      schema = @Schema(implementation = DealerDiscountPolicyBulkRequest.class),
                      examples = {
                        @ExampleObject(
                            name = "Bulk Policy Example",
                            value =
                                """
                                        {
                                          "dealerIds": [
                                            "550e8400-e29b-41d4-a716-446655440000",
                                            "550e8400-e29b-41d4-a716-446655440001"
                                          ],
                                          "vehicleModelIds": [
                                            "660e8400-e29b-41d4-a716-446655440000",
                                            "660e8400-e29b-41d4-a716-446655440001"
                                          ],
                                          "customMultiplier": 1.05,
                                          "finalPrice": 27000.00,
                                          "effectiveDate": "2025-10-15",
                                          "expiredDate": "2025-12-31"
                                        }
                                        """)
                      })))
  public ResponseEntity<APIResponse<List<DealerDiscountPolicyResponse>>> bulkCreate(
      @RequestBody @Valid DealerDiscountPolicyBulkRequest request) {
    APIResponse<List<DealerDiscountPolicyResponse>> responses =
        dealerDiscountPolicyService.bulkCreate(request);
    return ResponseEntity.ok(responses);
  }

  // ================= BULK UPDATE =================
  @PutMapping("/bulk-update")
  @Operation(
      summary = "Bulk update Dealer Discount Policies",
      description = "Update existing DealerDiscountPolicies for multiple dealers and vehicles",
      requestBody =
          @io.swagger.v3.oas.annotations.parameters.RequestBody(
              description = "DealerDiscountPolicyBulkRequest payload",
              required = true,
              content =
                  @Content(
                      mediaType = "application/json",
                      schema = @Schema(implementation = DealerDiscountPolicyBulkRequest.class),
                      examples = {
                        @ExampleObject(
                            name = "Bulk Update Example",
                            value =
                                """
                                        {
                                          "dealerIds": [
                                            "550e8400-e29b-41d4-a716-446655440000",
                                            "550e8400-e29b-41d4-a716-446655440001"
                                          ],
                                          "vehicleModelIds": [
                                            "660e8400-e29b-41d4-a716-446655440000",
                                            "660e8400-e29b-41d4-a716-446655440001"
                                          ],
                                          "customMultiplier": 1.10,
                                          "finalPrice": 26500.00,
                                          "effectiveDate": "2025-10-20",
                                          "expiredDate": "2026-01-31"
                                        }
                                        """)
                      })))
  public APIResponse<List<DealerDiscountPolicyResponse>> bulkUpdate(
      @RequestBody DealerDiscountPolicyBulkRequest request) {
    return dealerDiscountPolicyService.bulkUpdate(request);
  }

  // ================= BULK DELETE =================
  @PostMapping("/bulk-delete")
  @Operation(
      summary = "Bulk delete Dealer Discount Policies",
      description =
          "Set status to INACTIVE for multiple DealerDiscountPolicies based on dealerIds and"
              + " vehicleModelIds",
      requestBody =
          @io.swagger.v3.oas.annotations.parameters.RequestBody(
              description = "DealerDiscountPolicyBulkDeleteRequest payload",
              required = true,
              content =
                  @Content(
                      mediaType = "application/json",
                      schema =
                          @Schema(implementation = DealerDiscountPolicyBulkDeleteRequest.class),
                      examples = {
                        @ExampleObject(
                            name = "Example request",
                            value =
                                """
                        {
                          "dealerIds": ["uuid-1", "uuid-2"],
                          "vehicleModelIds": ["uuid-3", "uuid-4"]
                        }
                        """)
                      })))
  public ResponseEntity<APIResponse<List<DealerDiscountPolicyResponse>>> bulkDelete(
      @Valid @RequestBody DealerDiscountPolicyBulkDeleteRequest request) {
    APIResponse<List<DealerDiscountPolicyResponse>> response =
        dealerDiscountPolicyService.bulkDelete(request);
    return ResponseEntity.ok(response);
  }

  // =================  DELETE =================
  @DeleteMapping("/{id}")
  @Operation(
      summary = "Delete Dealer Discount Policy",
      description = "Set status of a DealerDiscountPolicy to INACTIVE by its ID",
      parameters = {
        @Parameter(
            name = "id",
            description = "UUID of the DealerDiscountPolicy to delete",
            required = true,
            example = "123e4567-e89b-12d3-a456-426614174000")
      })
  public ResponseEntity<APIResponse<DealerDiscountPolicyResponse>> delete(@PathVariable UUID id) {
    APIResponse<DealerDiscountPolicyResponse> response = dealerDiscountPolicyService.delete(id);
    return ResponseEntity.ok(response);
  }

  // =================  Update =================
  @PutMapping("/{id}")
  @Operation(
      summary = "Update Dealer Discount Policy",
      description = "Update a DealerDiscountPolicy by its ID",
      parameters = {
        @Parameter(
            name = "id",
            description = "UUID of the DealerDiscountPolicy to update",
            required = true,
            example = "123e4567-e89b-12d3-a456-426614174000")
      })
  public ResponseEntity<APIResponse<DealerDiscountPolicyResponse>> update(
      @PathVariable UUID id, @RequestBody @Valid DealerDiscountPolicyRequest request) {
    APIResponse<DealerDiscountPolicyResponse> response =
        dealerDiscountPolicyService.update(id, request);
    return ResponseEntity.ok(response);
  }

  // =================  Get All =================
  @GetMapping
  @Operation(
      summary = "Get all Dealer Discount Policies",
      description = "Retrieve paginated list of Dealer Discount Policies")
  public ResponseEntity<APIResponse<PageResponse<DealerDiscountPolicyResponse>>> getAll(
      @RequestParam(defaultValue = "0") int page,
      @RequestParam(defaultValue = "10") int size,
      @RequestParam(required = false) String keyword,
      @RequestParam(required = false) List<DiscountPolicyStatus> status,
      @RequestParam(defaultValue = "effectiveDate") String sortField,
      @RequestParam(defaultValue = "desc") String sortDir) {

    Sort sort = Sort.by(sortField);
    sort = "asc".equalsIgnoreCase(sortDir) ? sort.ascending() : sort.descending();

    Pageable pageable = PageRequest.of(page, size, sort);

    APIResponse<PageResponse<DealerDiscountPolicyResponse>> response =
        dealerDiscountPolicyService.getAll(pageable, keyword, status);

    return ResponseEntity.ok(response);
  }

    // =================  Get All By Dealer =================
    @GetMapping("/by-dealer")
    @Operation(
            summary = "Get all Dealer Discount Policies",
            description = "Retrieve paginated list of Dealer Discount Policies")
    public ResponseEntity<APIResponse<PageResponse<DealerDiscountPolicyResponse>>> getAllByDealer(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) List<DiscountPolicyStatus> status,
            @RequestParam(defaultValue = "effectiveDate") String sortField,
            @RequestParam(defaultValue = "desc") String sortDir) {

        Sort sort = Sort.by(sortField);
        sort = "asc".equalsIgnoreCase(sortDir) ? sort.ascending() : sort.descending();

        Pageable pageable = PageRequest.of(page, size, sort);

        APIResponse<PageResponse<DealerDiscountPolicyResponse>> response =
                dealerDiscountPolicyService.getAllByDealer(pageable, keyword, status);

        return ResponseEntity.ok(response);
    }

  // =================  Get by ID =================
  @GetMapping("/{id}")
  @Operation(
      summary = "Get Dealer Discount Policy by ID",
      description = "Retrieve a DealerDiscountPolicy by its ID",
      parameters = {
        @Parameter(
            name = "id",
            description = "UUID of the DealerDiscountPolicy",
            required = true,
            example = "123e4567-e89b-12d3-a456-426614174000")
      })
  public ResponseEntity<APIResponse<DealerDiscountPolicyResponse>> getById(@PathVariable UUID id) {
    APIResponse<DealerDiscountPolicyResponse> response = dealerDiscountPolicyService.get(id);
    return ResponseEntity.ok(response);
  }
}
