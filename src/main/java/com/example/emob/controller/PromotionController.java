package com.example.emob.controller;

import com.example.emob.model.request.promotion.PromotionRequest;
import com.example.emob.model.response.APIResponse;
import com.example.emob.model.response.PromotionResponse;
import com.example.emob.service.PromotionService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@CrossOrigin("*")
@RequestMapping("/api/dealer/promotion")
@SecurityRequirement(name = "api")
@Tag(name = "Promotion Controller", description = "Endpoints for managing promotions")
public class PromotionController {
    @Autowired
    PromotionService promotionService;

    @PostMapping
    public ResponseEntity<APIResponse<PromotionResponse>> createPromotion (@RequestBody @Valid PromotionRequest request) {
        return ResponseEntity.ok(promotionService.createPromotion(request));
    }

}
