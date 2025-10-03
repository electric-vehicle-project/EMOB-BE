package com.example.emob.mapper;

import com.example.emob.entity.Promotion;
import com.example.emob.model.request.promotion.PromotionRequest;
import com.example.emob.model.response.PromotionResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface PromotionMapper {
    @Mapping(source = "id", target = "promotionId")
    @Mapping(source = "dealer.id", target = "dealerId")
    PromotionResponse toPromotionResponse (Promotion promotion);

    @Mapping(target = "id", ignore = true)
    Promotion toPromotion (PromotionRequest request);
}
