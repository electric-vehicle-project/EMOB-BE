/* EMOB-2025 */
package com.example.emob.mapper;

import com.example.emob.entity.Dealer;
import com.example.emob.entity.ElectricVehicle;
import com.example.emob.entity.Promotion;
import com.example.emob.model.request.promotion.PromotionRequest;
import com.example.emob.model.request.promotion.UpdatePromotionRequest;
import com.example.emob.model.response.PromotionResponse;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface PromotionMapper {
  @Mapping(source = "id", target = "id")
  @Mapping(target = "vehicleIds", expression = "java(mapVehicleIds(promotion.getVehicles()))")
  @Mapping(target = "dealerIds", expression = "java(mapDealerIds(promotion.getDealers()))")
  PromotionResponse toPromotionResponse(Promotion promotion);

  Promotion toPromotion(PromotionRequest request);

  void updatePromotionFromRequest(
      UpdatePromotionRequest request, @MappingTarget Promotion promotion);

  default Set<UUID> mapVehicleIds(Set<ElectricVehicle> vehicles) {
    if (vehicles == null) {
      return new HashSet<>();
    }
    return vehicles.stream().map(ElectricVehicle::getId).collect(Collectors.toSet());
  }

  default Set<UUID> mapDealerIds(Set<Dealer> dealers) {
    if (dealers == null) return Set.of();
    return dealers.stream().map(Dealer::getId).collect(Collectors.toSet());
  }
}
