package com.example.emob.service.iml;

import com.example.emob.model.request.promotion.PromotionRequest;
import com.example.emob.model.response.APIResponse;
import com.example.emob.model.response.PromotionResponse;

public interface IPromotion {
    APIResponse<PromotionResponse> createPromotion (PromotionRequest request);
}
