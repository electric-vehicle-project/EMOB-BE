/* EMOB-2025 */
package com.example.emob.mapper;

import com.example.emob.entity.DealerPointRule;
import com.example.emob.model.request.DealerPointRequest;
import com.example.emob.model.response.DealerPointRuleResponse;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface DealerPointRuleMapper {
  DealerPointRule toDealerPointRule(DealerPointRequest request);

  DealerPointRuleResponse toDealerPointRuleResponse(DealerPointRule dealerPointRule);
}
