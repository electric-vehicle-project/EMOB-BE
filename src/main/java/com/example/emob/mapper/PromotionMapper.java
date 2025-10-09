/* EMOB-2025 */
package com.example.emob.mapper;

import com.example.emob.entity.Dealer;
import com.example.emob.entity.ElectricVehicle;
import com.example.emob.entity.Promotion;
import com.example.emob.model.request.promotion.PromotionRequest;
import com.example.emob.model.request.promotion.PromotionValueRequest;
import com.example.emob.model.request.promotion.UpdatePromotionRequest;
import com.example.emob.model.response.PromotionResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Named;

import java.util.Collections;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring")
public interface PromotionMapper {
    PromotionResponse toPromotionResponse (Promotion promotion);
//
//    @Named("dealersToIds")
//    default Set<UUID> dealersToIds(Set<Dealer> dealers) {
//        return dealers == null || dealers.isEmpty() ? Collections.emptySet() :
//                dealers.stream().map(Dealer::getId).collect(Collectors.toSet());
//    }
//    @Named("vehiclesToIds")
//    default Set<UUID> vehiclesToIds(Set<ElectricVehicle> vehicles) {
//        return vehicles == null || vehicles.isEmpty() ? Collections.emptySet() :
//                vehicles.stream().map(ElectricVehicle::getId).collect(Collectors.toSet());
//    }

    Promotion toPromotion (PromotionRequest request);


//    void updatePromotionFromRequest (UpdatePromotionRequest request, @MappingTarget Promotion promotion);
}
