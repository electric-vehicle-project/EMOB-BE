/* EMOB-2025 */
package com.example.emob.util;

import com.example.emob.constant.PromotionStatus;
import com.example.emob.mapper.PromotionMapper;
import com.example.emob.repository.PromotionRepository;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PromotionHelper {

  @Autowired private static PromotionRepository promotionRepository;

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

  public static BigDecimal calculateDiscountedPrice(
      BigDecimal price, Promotion promotion, Customer customer) {
    BigDecimal discountedPrice = price;

    if (promotion.getType() == PromotionType.PERCENTAGE) {
      discountedPrice =
          price.subtract(
              price
                  .multiply(BigDecimal.valueOf(promotion.getValue()))
                  .divide(BigDecimal.valueOf(100)));
    } else if (promotion.getType() == PromotionType.FIXED_AMOUNT) {
      discountedPrice = price.subtract(BigDecimal.valueOf(promotion.getValue()));
    } else if (promotion.getType() == PromotionType.POINT) {
      customer.setLoyaltyPoints((int) promotion.getValue());
      // Nếu chỉ cộng điểm, giá không thay đổi
      discountedPrice = price;
    }
    // Áp dụng giá trị tối thiểu
    if (discountedPrice.compareTo(BigDecimal.valueOf(promotion.getMinValue())) < 0) {
      discountedPrice = BigDecimal.valueOf(promotion.getMinValue());
    }
    String dealerId = customer.getDealer().getId().toString();
    String membershipLevel = customer.getMemberShipLevel().toString();
    DealerPointRule rule = dealerPointRuleService.getRule(dealerId, membershipLevel);
    discountedPrice = discountedPrice.subtract(rule.getPrice());

    return discountedPrice;
  }

  public static BigDecimal calculateDiscountedPrice(BigDecimal price, Customer customer) {
    BigDecimal discountedPrice = price;
    String dealerId = customer.getDealer().getId().toString();
    String membershipLevel = customer.getMemberShipLevel().toString();
    DealerPointRule rule = dealerPointRuleService.getRule(dealerId, membershipLevel);
    discountedPrice = discountedPrice.subtract(rule.getPrice());
    return discountedPrice;
  }
}
