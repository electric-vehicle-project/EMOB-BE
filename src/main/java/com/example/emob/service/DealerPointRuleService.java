/* EMOB-2025 */
package com.example.emob.service;

import com.example.emob.constant.ErrorCode;
import com.example.emob.entity.DealerPointRule;
import com.example.emob.exception.GlobalException;
import com.example.emob.mapper.DealerPointRuleMapper;
import com.example.emob.mapper.PageMapper;
import com.example.emob.model.request.DealerPointRuleRequest;
import com.example.emob.model.response.APIResponse;
import com.example.emob.repository.DealerPointRuleRepository;
import com.example.emob.service.impl.IDealerPointRule;
import java.util.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

@Service
public class DealerPointRuleService implements IDealerPointRule {
  @Autowired DealerPointRuleRepository dealerPointRepository;

  @Autowired PageMapper pageMapper;

  @Autowired DealerPointRuleMapper dealerPointRuleMapper;

  @PreAuthorize("hasRole('MANAGER')")
  @Override
  public APIResponse<String> saveRule(List<DealerPointRuleRequest> requests) {
    // Sử dụng Builder để tạo đối tượng một cách rõ ràng
    try {
      for (DealerPointRuleRequest req : requests) {
        String memberShipLevel = req.getLevel().toString();
        DealerPointRule rule =
                DealerPointRule.builder()
                        .membershipLevel(memberShipLevel)
                        .dealerId(req.getDealerId())
                        .minPoints(req.getMinPoints())
                        .price(req.getPrice())
                        .build();
        // Lưu đối tượng vào cơ sở dữ liệu
        dealerPointRepository.save(rule);
      }
    } catch (GlobalException ex) {
      System.out.println("Exception: " + ex.getMessage());
    } catch (Exception ex) {
      System.out.println("Exception: " + ex.getMessage());
    }
    return APIResponse.success("", "Create rule for dealer successfully");
  }

  @Override

  public APIResponse<List<DealerPointRule>> getRule(String dealerId) {
    List<DealerPointRule> rules = dealerPointRepository
        .findByDealerId(dealerId);
    if (rules.isEmpty()) {
      throw new GlobalException(ErrorCode.NOT_FOUND);
    }
    return APIResponse.success(rules);

  }

  @Override
  public DealerPointRule getRule(String dealerId, String membershipLevel) {
    return dealerPointRepository
        .findDealerPointRulesByDealerIdAndMembershipLevel(dealerId, membershipLevel)
        .orElseThrow(() -> new GlobalException(ErrorCode.NOT_FOUND));
  }

  @Override
  public APIResponse<List<DealerPointRule>> getAllRules() {
    try {
      List<DealerPointRule> rules = new ArrayList<>();
      dealerPointRepository.findAll().forEach(rules::add);
      return APIResponse.success(rules);
    } catch (Exception e) {
      throw new GlobalException(ErrorCode.OTHER, "Error: " + e.getMessage());
    }
  }
}
