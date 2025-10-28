/* EMOB-2025 */
package com.example.emob.service.impl;

import com.example.emob.constant.MemberShipLevel;
import com.example.emob.entity.DealerPointRule;
import com.example.emob.model.request.DealerPointRuleRequest;
import com.example.emob.model.response.APIResponse;
import com.example.emob.model.response.DealerPointRuleResponse;
import com.example.emob.model.response.PageResponse;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.util.List;

public interface IDealerPointRule {
  APIResponse<String> saveRule(
          List<DealerPointRuleRequest> requests);

  List<DealerPointRule> getRule(String dealerId);

  APIResponse<List<DealerPointRule>> getAllRules();

  DealerPointRule getRule(String dealerId, String membershipLevel);
}
