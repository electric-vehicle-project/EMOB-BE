/* EMOB-2025 */
package com.example.emob.mapper;

import com.example.emob.entity.Dealer;
import com.example.emob.entity.ElectricVehicle;
import com.example.emob.entity.Promotion;
import com.example.emob.model.request.promotion.PromotionRequest;
import com.example.emob.model.request.promotion.PromotionValueRequest;
import com.example.emob.model.request.promotion.UpdatePromotionRequest;
import com.example.emob.model.response.PromotionResponse;
import java.util.Collections;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Named;

@Mapper(componentModel = "spring")
public interface PromotionMapper {
    @Mapping(source = "id", target = "id")
    PromotionResponse toPromotionResponse (Promotion promotion);

    Promotion toPromotion (PromotionRequest request);

    void updatePromotionFromRequest (UpdatePromotionRequest request, @MappingTarget Promotion promotion);
}