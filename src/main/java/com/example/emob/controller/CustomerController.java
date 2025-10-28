/* EMOB-2025 */
package com.example.emob.controller;

import com.example.emob.constant.CustomerStatus;
import com.example.emob.model.request.CustomerRequest;
import com.example.emob.model.response.APIResponse;
import com.example.emob.model.response.CustomerResponse;
import com.example.emob.model.response.PageResponse;
import com.example.emob.service.CustomerService;
import io.swagger.v3.oas.annotations.Operation;
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
@RequestMapping("/api/customers")
@CrossOrigin("*")
@Tag(name = "Customer Controller", description = "Endpoints for managing customers")
@SecurityRequirement(name = "api")
public class CustomerController {
  @Autowired CustomerService customerService;

  @PostMapping
  @Operation(
      summary = "Create new Customer",
      requestBody =
          @io.swagger.v3.oas.annotations.parameters.RequestBody(
              description = "Create a new Customer",
              required = true,
              content =
                  @Content(
                      mediaType = "application/json",
                      schema = @Schema(implementation = CustomerRequest.class),
                      examples = {
                        @ExampleObject(
                            name = "Customer A",
                            value =
                                """
                                                    {
                                                      "fullName": "Alice",
                                                      "email": "alice@example.com",
                                                      "phoneNumber": "0987654321",
                                                      "address": "123 Elm St",
                                                      "note": "Loyal customer",
                                                      "dateOfBirth": "1990-01-01",
                                                      "gender": "FEMALE",
                                                      "loyaltyPoints": 100
                                                    }
                                                    """),
                        @ExampleObject(
                            name = "Customer B",
                            value =
                                """
                                                    {
                                                      "fullName": "Bob",
                                                      "email": "bob@example.com",
                                                      "phoneNumber": "0123456789",
                                                      "address": "456 Oak St",
                                                      "note": "New member",
                                                      "dateOfBirth": "1992-02-02",
                                                      "gender": "MALE"
                                                    }
                                                    """)
                      })))
  public ResponseEntity<APIResponse<CustomerResponse>> create(
      @Valid @RequestBody CustomerRequest request) {
    return ResponseEntity.ok(customerService.create(request));
  }

  @GetMapping("/{id}")
  @Operation(summary = "Get Customer by ID")
  public ResponseEntity<APIResponse<CustomerResponse>> getById(@PathVariable UUID id) {
    return ResponseEntity.ok(customerService.get(id));
  }

  @GetMapping
  @Operation(summary = "Get all Customers (with pagination, filter, search, sort)")
  public ResponseEntity<APIResponse<PageResponse<CustomerResponse>>> getAll(
          @RequestParam(defaultValue = "0") int page,
          @RequestParam(defaultValue = "10") int size,
          @RequestParam(required = false) String keyword,
          @RequestParam(required = false) List<CustomerStatus> status,
          @RequestParam(defaultValue = "fullName") String sortField,
          @RequestParam(defaultValue = "desc") String sortDir) {

    Sort sort = Sort.by(sortField);
    sort = "asc".equalsIgnoreCase(sortDir) ? sort.ascending() : sort.descending();
    Pageable pageable = PageRequest.of(page, size, sort);

    return ResponseEntity.ok(customerService.getAll(pageable, keyword, status));
  }

  @PutMapping("/{id}")
  @Operation(summary = "Update existing Customer by ID")
  public ResponseEntity<APIResponse<CustomerResponse>> update(
      @PathVariable UUID id, @Valid @RequestBody CustomerRequest request) {
    return ResponseEntity.ok(customerService.update(id, request));
  }

  @DeleteMapping("/{id}")
  @Operation(summary = "Delete Customer by ID")
  public ResponseEntity<APIResponse<Void>> delete(@PathVariable UUID id) {
    customerService.delete(id);
    return ResponseEntity.ok(
        APIResponse.<Void>builder().code(200).message("Deleted successfully").build());
  }
}
