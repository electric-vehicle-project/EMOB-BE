/* EMOB-2025 */
package com.example.emob.service.iml;

import com.example.emob.model.request.promotion.PromotionRequest;
import com.example.emob.model.request.promotion.PromotionValueRequest;
import com.example.emob.model.request.promotion.UpdatePromotionRequest;
import com.example.emob.model.response.APIResponse;
import com.example.emob.model.response.PageResponse;
import com.example.emob.model.response.PromotionResponse;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface IPromotion {
    APIResponse<PromotionResponse> createPromotion (PromotionRequest request);

    APIResponse<PromotionResponse> updatePromotion (UpdatePromotionRequest request, UUID id);

    APIResponse<PromotionResponse> deletePromotion (UUID id);

    APIResponse<PromotionResponse> viewPromotion (UUID id);

    APIResponse<PageResponse<PromotionResponse>> viewAllLocalPromotions (Pageable pageable);

    APIResponse<PromotionResponse> createValuePromotion (UUID id, PromotionValueRequest request);

    APIResponse<PageResponse<PromotionResponse>> viewAllGlobalPromotions(Pageable pageable);
}
