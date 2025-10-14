/* EMOB-2025 */
package com.example.emob.util;

import com.example.emob.constant.MemberShipLevel;
import com.example.emob.constant.PromotionStatus;
import com.example.emob.entity.Promotion;
import com.example.emob.mapper.PromotionMapper;
import com.example.emob.repository.PromotionRepository;
import java.time.LocalDateTime;
import org.springframework.beans.factory.annotation.Autowired;

public class PromotionHelper {
  @Autowired private static PromotionMapper promotionMapper;

  @Autowired private static PromotionRepository promotionRepository;

  public static void calculateDiscountForCustomer(
      MemberShipLevel memberShipLevel, Promotion promotion, float default_value) {
    float discount =
        switch (memberShipLevel) {
          case GOLD -> 0.7f;
          case BRONZE -> 0.5f;
          case SILVER -> 0.6f;
          case PLATINUM -> 0.8f;
          default -> 0f;
        };
    promotion.setValue(discount * default_value);
  }

  public static PromotionStatus determinePromotionStatus(
      LocalDateTime startDate, LocalDateTime endDate) {
    if (startDate.isAfter(LocalDateTime.now())) return PromotionStatus.UPCOMING;
    if (endDate.isBefore(LocalDateTime.now())) return PromotionStatus.EXPIRED;
    return PromotionStatus.ACTIVE;
  }
}
