/* EMOB-2025 */
package com.example.emob.controller;

import com.example.emob.constant.VehicleStatus;
import com.example.emob.entity.VehiclePriceRule;
import com.example.emob.model.request.VehiclePriceRuleRequest;
import com.example.emob.model.response.APIResponse;
import com.example.emob.service.VehiclePriceRuleService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@CrossOrigin("*")
@RequestMapping("/api/vehicle-price-rules")
@SecurityRequirement(name = "api")
@Tag(
    name = "Vehicle Price Rule Controller",
    description = "Endpoints for managing vehicle price rules")
public class VehiclePriceRuleController {
  @Autowired private VehiclePriceRuleService vehiclePriceRuleService;

  @GetMapping
  @Operation(
      summary = "Get All Vehicle Price Rules",
      description = "Return all vehicle price rules")
  public ResponseEntity<APIResponse<List<VehiclePriceRule>>> getAllRules() {
    return ResponseEntity.ok(vehiclePriceRuleService.getAllRules());
  }

  @GetMapping("/{status}")
  @Operation(
      summary = "Get Vehicle Price Rule by Status",
      description = "Return price rule for a specific vehicle status")
  public ResponseEntity<APIResponse<VehiclePriceRule>> getRuleByStatus(
      @PathVariable VehicleStatus status) {
    VehiclePriceRule rule = vehiclePriceRuleService.getRule(status);
    return ResponseEntity.ok(APIResponse.success(rule));
  }

  @PutMapping
  @Operation(
      summary = "Create Vehicle Price Rule",
      requestBody =
          @io.swagger.v3.oas.annotations.parameters.RequestBody(
              description = "Create new vehicle price rule",
              required = true,
              content =
                  @Content(
                      mediaType = "application/json",
                      schema = @Schema(implementation = VehiclePriceRule.class),
                      examples = {
@ExampleObject(
    name = "Rules Array",
    value =
        """
        [
          {
            "vehicleStatus": "NORMAL",
            "multiplier": 1.0,
            "note": "High season pricing"
          },
          {
            "vehicleStatus": "TEST_DRIVE",
            "multiplier": 0.9,
            "note": "High season pricing"
          },
          {
            "vehicleStatus": "SPECIAL",
            "multiplier": 1.5,
            "note": "High season pricing"
          },
          {
            "vehicleStatus": "OLD_STOCK",
            "multiplier": 0.8,
            "note": "High season pricing"
          }
        ]
        """)
})))

  public ResponseEntity<APIResponse<String>> saveRule(
      @RequestBody List<VehiclePriceRuleRequest> vehiclePriceRuleRequests) {
    vehiclePriceRuleService.saveRule(vehiclePriceRuleRequests);
    return ResponseEntity.ok(APIResponse.success("Vehicle price rule created successfully"));
  }
}
