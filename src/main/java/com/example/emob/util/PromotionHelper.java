/* EMOB-2025 */
package com.example.emob.util;

import com.example.emob.constant.*;
import com.example.emob.entity.Customer;
import com.example.emob.entity.DealerPointRule;
import com.example.emob.entity.Promotion;
import com.example.emob.exception.GlobalException;
import com.example.emob.service.DealerPointRuleService;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PromotionHelper {

  private static DealerPointRuleService dealerPointRuleService;

  public static PromotionStatus determinePromotionStatus(
      LocalDateTime startDate, LocalDateTime endDate) {
    if (startDate.isAfter(LocalDateTime.now())) return PromotionStatus.UPCOMING;
    if (endDate.isBefore(LocalDateTime.now())) return PromotionStatus.EXPIRED;
    return PromotionStatus.ACTIVE;
  }

  public static void checkPromotionValid(Promotion promotion) {
    if (promotion.getStatus() == PromotionStatus.EXPIRED) {
      throw new GlobalException(ErrorCode.DATA_INVALID, "Promotion is expired");
    }
    if (promotion.getStatus() == PromotionStatus.INACTIVE) {
      throw new GlobalException(ErrorCode.DATA_INVALID, "Promotion is inactive");
    }
    if (promotion.getStatus() == PromotionStatus.UPCOMING) {
      throw new GlobalException(ErrorCode.DATA_INVALID, "Promotion is upcoming");
    }
  }

  public static BigDecimal calculateDiscountedPrice(BigDecimal price, Promotion promotion, Customer customer) {
    BigDecimal discountedPrice = price;

    if (promotion != null) {
      switch (promotion.getType()) {
        case PERCENTAGE -> discountedPrice = price.subtract(
                price.multiply(BigDecimal.valueOf(promotion.getValue()))
                        .divide(BigDecimal.valueOf(100))
        );
        case FIXED_AMOUNT -> discountedPrice = price.subtract(BigDecimal.valueOf(promotion.getValue()));
        case POINT -> {
          customer.setLoyaltyPoints((int) promotion.getValue());
          discountedPrice = price;
        }
      }

      if (discountedPrice.compareTo(BigDecimal.valueOf(promotion.getMinValue())) < 0) {
        discountedPrice = BigDecimal.valueOf(promotion.getMinValue());
      }
    }

    return calculateDiscountedByPoint(discountedPrice, customer);
  }

  private static BigDecimal calculateDiscountedByPoint(BigDecimal price, Customer customer) {
    BigDecimal discountedPriceByPoint = price;
    if (!MemberShipLevel.NORMAL.equals(customer.getMemberShipLevel())) {
      String dealerId = customer.getDealer().getId().toString();
      String membershipLevel = customer.getMemberShipLevel().toString();
      DealerPointRule rule = dealerPointRuleService.getRule(dealerId, membershipLevel);
      discountedPriceByPoint = price.subtract(rule.getPrice());
    }
    return discountedPriceByPoint;
  }
}

