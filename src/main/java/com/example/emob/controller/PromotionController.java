/* EMOB-2025 */
package com.example.emob.controller;

import com.example.emob.constant.PromotionScope;
import com.example.emob.model.request.promotion.PromotionRequest;
import com.example.emob.model.request.promotion.PromotionValueRequest;
import com.example.emob.model.request.promotion.UpdatePromotionRequest;
import com.example.emob.model.response.APIResponse;
import com.example.emob.model.response.PageResponse;
import com.example.emob.model.response.PromotionResponse;
import com.example.emob.service.PromotionService;
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
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@CrossOrigin("*")
@RequestMapping("/api/promotion")
@SecurityRequirement(name = "api")
@Tag(name = "Promotion Controller", description = "Endpoints for managing promotions")
public class PromotionController {
  @Autowired PromotionService promotionService;

  @PreAuthorize("hasAnyRole('EVM_STAFF', 'DEALER_STAFF')")
  @PostMapping
  @Operation(
      summary = "Generate Promotion",
      requestBody =
          @io.swagger.v3.oas.annotations.parameters.RequestBody(
              description = "Generate Promotion",
              required = true,
              content =
                  @Content(
                      mediaType = "application/json",
                      schema = @Schema(implementation = PromotionRequest.class),
                      examples = {
                        @ExampleObject(
                            name = "Promotion A",
                            value =
                                """
                                                          
                                                            {
                                                              "dealerId": [
                                                                "Global thì không truyền gì vào hết"
                                                              ],
                                                              "electricVehiclesId": [
                                                                "mặc định là phải xe"
                                                              ],
                                                              "name": "cấm trùng nha",
                                                              "description": "string"
                                                            }
                                                          
                                                          """),
                        @ExampleObject(
                            name = "Promotion B",
                            value =
                                """
                                                          
                                                            {
                                                              "dealerId": [
                                                                "Local thì truyền 1"
                                                              ],
                                                              "electricVehiclesId": [
                                                                "mặc định là phải xe"
                                                              ],
                                                              "name": "cấm trùng nha",
                                                              "description": "string"
                                                            }
                                                          
                                                          """),
                      })))
  public ResponseEntity<APIResponse<PromotionResponse>> createPromotion(
      @RequestBody @Valid PromotionRequest request) {
    return ResponseEntity.ok(promotionService.createPromotion(request));
  }

  @PreAuthorize("hasAnyRole('EVM_STAFF', 'DEALER_STAFF')")
  @PutMapping("/{id}")
  @Operation(summary = "Update Promotion")
  public ResponseEntity<APIResponse<PromotionResponse>> updatePromotion(
      @RequestBody @Valid UpdatePromotionRequest request, @PathVariable("id") UUID id) {
    return ResponseEntity.ok(promotionService.updatePromotion(request, id));
  }

  @PreAuthorize("hasAnyRole('EVM_STAFF', 'DEALER_STAFF')")
  @GetMapping("/{id}")
  @Operation(summary = "View Promotion")
  public ResponseEntity<APIResponse<PromotionResponse>> viewPromotion(@PathVariable("id") UUID id) {
    return ResponseEntity.ok(promotionService.viewPromotion(id));
  }

  @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
  @DeleteMapping("/{id}")
  @Operation(summary = "Delete Promotion")
  public ResponseEntity<APIResponse<Void>> deletePromotion(@PathVariable("id") UUID id) {
    return ResponseEntity.ok(promotionService.deletePromotion(id));
  }

  @GetMapping("/view-all/{scope}")
  @Operation(summary = "View All Promotion")
  public ResponseEntity<APIResponse<PageResponse<PromotionResponse>>> viewAllLocalPromotions(
      @RequestParam(defaultValue = "0") int page,
      @RequestParam(defaultValue = "10") int size,
      @RequestParam @PathVariable("scope") List<PromotionScope> scope) {
    Pageable pageResponse = PageRequest.of(page, size);
    return ResponseEntity.ok(promotionService.viewAllPromotions(pageResponse, scope));
  }

  @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
  @PutMapping("/value/{id}")
  public ResponseEntity<APIResponse<PromotionResponse>> createValuePromotion(
      @RequestBody @Valid PromotionValueRequest request, @PathVariable("id") UUID id) {
    return ResponseEntity.ok(promotionService.createValuePromotion(id, request));
  }

  @PreAuthorize("hasAnyRole('DEALER_STAFF', 'MANAGER')")
  @GetMapping("/history")
  @Operation(summary = "View History Dealer Promotion")
  public ResponseEntity<APIResponse<List<PromotionResponse>>> viewHistoryDealerPromotion(UUID id) {
    return ResponseEntity.ok(promotionService.viewHistoryDealerPromotion(id));
  }
}
