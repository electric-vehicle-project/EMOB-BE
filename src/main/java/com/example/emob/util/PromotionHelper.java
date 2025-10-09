/* EMOB-2025 */
package com.example.emob.util;

import com.example.emob.constant.ErrorCode;
import com.example.emob.constant.MemberShipLevel;
import com.example.emob.constant.PromotionScope;
import com.example.emob.constant.PromotionStatus;
import com.example.emob.entity.Promotion;
import com.example.emob.exception.GlobalException;
import com.example.emob.mapper.PromotionMapper;
import com.example.emob.model.response.PromotionResponse;
import java.time.LocalDateTime;
import org.springframework.beans.factory.annotation.Autowired;

public class PromotionHelper {
  @Autowired private static PromotionMapper promotionMapper;

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



  public static void checkPromotionValid(Promotion promotion) {
      if(promotion.getStatus() == PromotionStatus.EXPIRED){
        throw new GlobalException(ErrorCode.DATA_INVALID, "Promotion is expired");
    }
        if(promotion.getStatus() == PromotionStatus.INACTIVE){
        throw new GlobalException(ErrorCode.DATA_INVALID, "Promotion is inactive");
    }
        if(promotion.getStatus() == PromotionStatus.UPCOMING){
        throw new GlobalException(ErrorCode.DATA_INVALID, "Promotion is upcoming");
    }
  }

  public static void responseMemberShipLevel(
      Promotion promotion, PromotionResponse promotionResponse) {
    if (promotion.getScope().equals(PromotionScope.GLOBAL)) {
      promotionResponse.setMemberShipLevel(MemberShipLevel.NORMAL);
    } else if (promotion.getScope().equals(PromotionScope.LOCAL)) {
      promotionResponse.setMemberShipLevel(promotion.getMemberShipLevel());
    }
  }
}
