/* EMOB-2025 */
package com.example.emob.service.impl;

import com.example.emob.model.request.promotion.PromotionRequest;
import com.example.emob.model.request.promotion.PromotionValueRequest;
import com.example.emob.model.request.promotion.UpdatePromotionRequest;
import com.example.emob.model.response.APIResponse;
import com.example.emob.model.response.PageResponse;
import com.example.emob.model.response.PromotionResponse;
import java.util.UUID;
import org.springframework.data.domain.Pageable;

public interface IPromotion {
  APIResponse<PromotionResponse> createPromotion (PromotionRequest request);

  APIResponse<PromotionResponse> updatePromotion (UpdatePromotionRequest request, UUID id);

  APIResponse<Void> deletePromotion (UUID id);

  APIResponse<PromotionResponse> viewPromotion (UUID id);

  APIResponse<PageResponse<PromotionResponse>> viewAllLocalPromotions (Pageable pageable);

  APIResponse<PromotionResponse> createValuePromotion (UUID id, PromotionValueRequest request);

  APIResponse<PageResponse<PromotionResponse>> viewAllGlobalPromotions(Pageable pageable);
}
