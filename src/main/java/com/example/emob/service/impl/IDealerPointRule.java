/* EMOB-2025 */
package com.example.emob.service.impl;

import com.example.emob.constant.MemberShipLevel;
import com.example.emob.entity.DealerPointRule;
import com.example.emob.model.response.APIResponse;
import java.math.BigDecimal;
import java.util.List;

public interface IDealerPointRule {
  APIResponse<String> saveRule(
      MemberShipLevel level, String dealerId, int minPoints, BigDecimal price);

  List<DealerPointRule> getRule(String dealerId);

  APIResponse<List<DealerPointRule>> getAllRules();

  DealerPointRule getRule(String dealerId, String membershipLevel);
}
