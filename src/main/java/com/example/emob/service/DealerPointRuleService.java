package com.example.emob.service;

import com.example.emob.constant.ErrorCode;
import com.example.emob.constant.MemberShipLevel;
import com.example.emob.entity.DealerPointRule;
import com.example.emob.exception.GlobalException;
import com.example.emob.model.response.APIResponse;
import com.example.emob.repository.DealerPointRuleRepository;
import com.example.emob.service.impl.IDealerPointRule;
import com.example.emob.util.DealerPointUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.*;

@Service
public class DealerPointRuleService implements IDealerPointRule {
    @Autowired
    DealerPointRuleRepository dealerPointRepository;
    public APIResponse<String> saveRule(MemberShipLevel level, String dealerId, int minPoints, BigDecimal price) {
        // Sử dụng Builder để tạo đối tượng một cách rõ ràng
        try {
            DealerPointRule rule = DealerPointRule.builder()
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

    public DealerPointRule getRule(MemberShipLevel level) {
        return dealerPointRepository.findById(level.toString())
                .orElseThrow(() -> new GlobalException(ErrorCode.NOT_FOUND));
    }

    public APIResponse<List<DealerPointRule>> getAllRules() {
        List<DealerPointRule> rules = new ArrayList<>();
        // tìm xong lấy phàn tử và thêm vào
        dealerPointRepository.findAll().forEach(rules::add);
        return APIResponse.success(rules);
    }


    public BigDecimal calculateDiscountPriceByLevel (MemberShipLevel level,
                                                 int currentPoints, BigDecimal originalPrice) {
        DealerPointRule rule = getRule(level);
        // Kiểm tra xem người dùng đã đủ điểm để được giảm giá ở cấp này chưa
        if (currentPoints >= rule.getMinPoints()) {
            // nếu đủ điểm thì tính
            BigDecimal discountRate = rule.getPrice().divide(BigDecimal.valueOf(100));
            BigDecimal discountAmount = originalPrice.multiply(discountRate);
            BigDecimal discountedPrice = originalPrice.subtract(discountAmount);

            return discountedPrice;
        } else {
            return originalPrice;
        }
    }

    public int updateRankByPoint (MemberShipLevel level) {
        DealerPointRule rule = getRule(level);
        int currentPoints = rule.getMinPoints();
        // check membership đang level nào by point
        int nextMemberShipLevel = DealerPointUtil.nextLevel(level);
        return nextMemberShipLevel - currentPoints;
    }
}
