/* EMOB-2025 */
package com.example.emob.service;

import com.example.emob.constant.ErrorCode;
import com.example.emob.constant.MemberShipLevel;
import com.example.emob.entity.DealerPointRule;
import com.example.emob.exception.GlobalException;
import com.example.emob.model.response.APIResponse;
import com.example.emob.repository.DealerPointRuleRepository;
import com.example.emob.service.impl.IDealerPointRule;
import java.math.BigDecimal;
import java.util.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class DealerPointRuleService implements IDealerPointRule {
  @Autowired static DealerPointRuleRepository dealerPointRepository;

  public APIResponse<String> saveRule(
      MemberShipLevel level, String dealerId, int minPoints, BigDecimal price) {
    // Sử dụng Builder để tạo đối tượng một cách rõ ràng
    try {
      DealerPointRule rule =
          DealerPointRule.builder()
              .dealerId(dealerId)
              .membershipLevel(level.toString())
              .minPoints(minPoints)
              .price(price)
              .build();

      // Lưu đối tượng vào cơ sở dữ liệu
      dealerPointRepository.save(rule);
    } catch (Exception ex) {
      System.out.println("Exception: " + ex.getMessage());
    }
    return APIResponse.success("", "Create rule for dealer successfully");
  }

  @Override
  public List<DealerPointRule> getRule(String dealerId) {
    return dealerPointRepository
        .findByDealerId(dealerId)
        .orElseThrow(() -> new GlobalException(ErrorCode.NOT_FOUND));
  }

  @Override
  public DealerPointRule getRule(String dealerId, String membershipLevel) {
    return dealerPointRepository
        .findDealerPointRulesByDealerIdAndMembershipLevel(dealerId, membershipLevel)
        .orElseThrow(() -> new GlobalException(ErrorCode.NOT_FOUND));
  }

  public APIResponse<List<DealerPointRule>> getAllRules() {
    List<DealerPointRule> rules = new ArrayList<>();
    // tìm xong lấy phàn tử và thêm vào
    dealerPointRepository.findAll().forEach(rules::add);
    return APIResponse.success(rules);
  }

  public static MemberShipLevel determineMembership(int point, String dealerId) {
    List<DealerPointRule> rules =
        dealerPointRepository
            .findByDealerId(dealerId)
            .orElseThrow(() -> new GlobalException(ErrorCode.NOT_FOUND, "Dealer not found"));

    // Sắp xếp theo minPoints giảm dần
    rules.sort(Comparator.comparingInt(DealerPointRule::getMinPoints).reversed());

    for (DealerPointRule rule : rules) {
      if (point >= rule.getMinPoints()) {
        return MemberShipLevel.valueOf(rule.getMembershipLevel());
      }
    }

    return MemberShipLevel.NORMAL;
  }
}
