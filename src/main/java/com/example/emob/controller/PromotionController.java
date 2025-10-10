/* EMOB-2025 */
package com.example.emob.controller;

import com.example.emob.model.request.promotion.PromotionRequest;
import com.example.emob.model.request.promotion.PromotionValueRequest;
import com.example.emob.model.request.promotion.UpdatePromotionRequest;
import com.example.emob.model.response.APIResponse;
import com.example.emob.model.response.PageResponse;
import com.example.emob.model.response.PromotionResponse;
import com.example.emob.service.PromotionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@CrossOrigin("*")
@RequestMapping("/api/promotion")
@SecurityRequirement(name = "api")
@Tag(name = "Promotion Controller", description = "Endpoints for managing promotions")
public class PromotionController {
  @Autowired PromotionService promotionService;

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
                      schema = @Schema(implementation = PromotionRequest.class))))
  public ResponseEntity<APIResponse<PromotionResponse>> createPromotion(
      @RequestBody @Valid PromotionRequest request) {
    return ResponseEntity.ok(promotionService.createPromotion(request));
  }

    @PutMapping("/{id}")
    @Operation(summary = "Update Promotion")
    public ResponseEntity<APIResponse<PromotionResponse>> updatePromotion (@RequestBody @Valid UpdatePromotionRequest request,
                                                                            @PathVariable("id") UUID id) {
        return ResponseEntity.ok(promotionService.updatePromotion(request, id));
    }

  @GetMapping("/{id}")
  @Operation(summary = "View Promotion")
  public ResponseEntity<APIResponse<PromotionResponse>> viewPromotion(@PathVariable("id") UUID id) {
    return ResponseEntity.ok(promotionService.viewPromotion(id));
  }

  @DeleteMapping("/{id}")
  @Operation(summary = "Delete Promotion")
  public ResponseEntity<APIResponse<PromotionResponse>> deletePromotion(
      @PathVariable("id") UUID id) {
    return ResponseEntity.ok(promotionService.deletePromotion(id));
  }

    @GetMapping("/view-local-all")
    @Operation(summary = "View All Promotion")
    public ResponseEntity<APIResponse<PageResponse<PromotionResponse>>> viewAllLocalPromotions (@RequestParam(defaultValue = "0") int page,
                                                                                           @RequestParam(defaultValue = "10") int size) {
        Pageable pageResponse = PageRequest.of(page, size);
        return ResponseEntity.ok(promotionService.viewAllLocalPromotions(pageResponse));
    }

    @GetMapping("/view-global-all")
    @Operation(summary = "View All Global Promotion")
    public ResponseEntity<APIResponse<PageResponse<PromotionResponse>>> viewAllGlobalPromotions (@RequestParam(defaultValue = "0") int page,
                                                                                           @RequestParam(defaultValue = "10") int size) {
        Pageable pageResponse = PageRequest.of(page, size);
        return ResponseEntity.ok(promotionService.viewAllGlobalPromotions(pageResponse));
    }
    @PutMapping("/value/{id}")
        public ResponseEntity<APIResponse<PromotionResponse>> createValuePromotion (@RequestBody @Valid PromotionValueRequest request,
                                                                                       @PathVariable("id") UUID id) {
        return ResponseEntity.ok(promotionService.createValuePromotion(id, request));
    }
}
