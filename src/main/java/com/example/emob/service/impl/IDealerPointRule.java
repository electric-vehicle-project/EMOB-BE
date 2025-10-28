/* EMOB-2025 */
package com.example.emob.service.impl;

import com.example.emob.entity.DealerPointRule;
import com.example.emob.model.request.DealerPointRuleRequest;
import com.example.emob.model.response.APIResponse;
import java.util.List;

public interface IDealerPointRule {
  APIResponse<String> saveRule(List<DealerPointRuleRequest> requests);

  List<DealerPointRule> getRule(String dealerId);

  APIResponse<List<DealerPointRule>> getAllRules();

  DealerPointRule getRule(String dealerId, String membershipLevel);
}
