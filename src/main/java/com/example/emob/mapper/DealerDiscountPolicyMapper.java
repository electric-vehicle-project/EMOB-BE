/* EMOB-2025 */
package com.example.emob.mapper;

import com.example.emob.entity.DealerDiscountPolicy;
import com.example.emob.model.response.DealerDiscountPolicyResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface DealerDiscountPolicyMapper {
  @Mapping(target = "dealerId", source = "dealer.id")
  @Mapping(target = "vehicleId", source = "vehicle.id")
  DealerDiscountPolicyResponse toResponse(DealerDiscountPolicy policy);
}
