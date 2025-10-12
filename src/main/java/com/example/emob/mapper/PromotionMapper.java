/* EMOB-2025 */
package com.example.emob.mapper;

import com.example.emob.entity.Promotion;
import com.example.emob.model.request.promotion.PromotionRequest;
import com.example.emob.model.request.promotion.UpdatePromotionRequest;
import com.example.emob.model.response.PromotionResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface PromotionMapper {
    @Mapping(source = "id", target = "id")
    PromotionResponse toPromotionResponse (Promotion promotion);

    Promotion toPromotion (PromotionRequest request);

    void updatePromotionFromRequest (UpdatePromotionRequest request, @MappingTarget Promotion promotion);
}