/* EMOB-2025 */
package com.example.emob.controller;

import com.example.emob.entity.DealerPointRule;
import com.example.emob.model.request.DealerPointRequest;
import com.example.emob.model.response.APIResponse;
import com.example.emob.model.response.DealerPointRuleResponse;
import com.example.emob.model.response.PageResponse;
import com.example.emob.service.DealerPointRuleService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/dealer-point-rules")
@CrossOrigin("*")
@Tag(
    name = "Dealer Point Rules Controller",
    description = "Endpoints for managing dealer point rules")
@SecurityRequirement(name = "api")
public class DealerPointController {
  @Autowired private DealerPointRuleService dealerPointRuleService;

  @PostMapping
  @Operation(
      summary = "Create Dealer Point Rule",
      requestBody =
          @io.swagger.v3.oas.annotations.parameters.RequestBody(
              description = "Create new dealer point rule",
              required = true,
              content =
                  @Content(
                      mediaType = "application/json",
                      schema = @Schema(implementation = DealerPointRequest.class))))
  public ResponseEntity<APIResponse<String>> saveRule(
      @RequestBody @Valid DealerPointRequest request) {
    return ResponseEntity.ok(
        dealerPointRuleService.saveRule(
            request.getMembershipLevel(),
            request.getDealerId(),
            request.getMinPoints(),
            request.getPrice()));
  }

  @GetMapping("/{dealerId}")
  @Operation(summary = "Get Dealer Point Rule", description = "Get Dealer Point Rule By Status")
  public ResponseEntity<APIResponse<List<DealerPointRule>>> getRule(@PathVariable String dealerId) {
    return ResponseEntity.ok(APIResponse.success(dealerPointRuleService.getRule(dealerId)));
  }

  @GetMapping
  @Operation(
      summary = "Get All Dealer Point Rules",
      description = "Get All Dealer Point Rules By Status")
  public ResponseEntity<APIResponse<PageResponse<DealerPointRuleResponse>>> getAllRules(
          @RequestParam(defaultValue = "0") int page,
          @RequestParam(defaultValue = "10") int size,
          @RequestParam(required = false) String keyword,
          @RequestParam(required = false) Integer minPoints,
          @RequestParam(defaultValue = "membershipLevel") String sortField,
          @RequestParam(defaultValue = "asc") String sortDir) {

    Sort sort = Sort.by(sortField);
    sort = "asc".equalsIgnoreCase(sortDir) ? sort.ascending() : sort.descending();
    Pageable pageable = PageRequest.of(page, size, sort);

    APIResponse<PageResponse<DealerPointRuleResponse>> response =
            dealerPointRuleService.getAllRules(pageable, keyword, minPoints);

    return ResponseEntity.ok(response);
  }
}
