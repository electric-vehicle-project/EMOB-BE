/* EMOB-2025 */
package com.example.emob.service.impl;

import com.example.emob.constant.MemberShipLevel;
import com.example.emob.entity.DealerPointRule;
import com.example.emob.model.response.APIResponse;
import com.example.emob.model.response.DealerPointRuleResponse;
import com.example.emob.model.response.PageResponse;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.util.List;

public interface IDealerPointRule {
  APIResponse<String> saveRule(
      MemberShipLevel level, String dealerId, int minPoints, BigDecimal price);

  List<DealerPointRule> getRule(String dealerId);

  APIResponse<PageResponse<DealerPointRuleResponse>> getAllRules(Pageable pageable, String keyword, Integer minPoints);

  DealerPointRule getRule(String dealerId, String membershipLevel);
}
