/* EMOB-2025 */
package com.example.emob.util;

import com.example.emob.constant.ErrorCode;
import com.example.emob.constant.MemberShipLevel;
import com.example.emob.constant.PromotionStatus;
import com.example.emob.constant.PromotionType;
import com.example.emob.entity.Promotion;
import com.example.emob.exception.GlobalException;
import com.example.emob.mapper.PromotionMapper;
import com.example.emob.repository.PromotionRepository;
import java.time.LocalDateTime;
import org.springframework.beans.factory.annotation.Autowired;

public class PromotionHelper {
    public static PromotionStatus determinePromotionStatus (LocalDateTime startDate, LocalDateTime endDate) {
        if (startDate.isAfter(LocalDateTime.now())) return PromotionStatus.UPCOMING;
        if (endDate.isBefore(LocalDateTime.now()))   return PromotionStatus.EXPIRED;
        return PromotionStatus.ACTIVE;
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
  public  static double calculateDiscountedPrice(double price, Promotion promotion, MemberShipLevel memberShipLevel) {
      double discountedPrice = price;
      if (promotion.getType() == PromotionType.PERCENTAGE) {
          discountedPrice = price - (price * promotion.getValue() / 100);
      } else if (promotion.getType() == PromotionType.FIXED_AMOUNT) {
          discountedPrice = price - promotion.getValue();
      }
      // Áp dụng giới hạn giá trị tối thiểu
      if (discountedPrice < promotion.getMinValue()) {
          discountedPrice = promotion.getMinValue();
      }
      // Áp dụng giới hạn giá trị tối đa dựa trên cấp độ thành viên
      if (memberShipLevel == MemberShipLevel.GOLD && discountedPrice > 5000) {
          discountedPrice = 5000;
      } else if (memberShipLevel == MemberShipLevel.SILVER && discountedPrice > 3000) {
          discountedPrice = 3000;
      } else if (memberShipLevel == MemberShipLevel.BRONZE && discountedPrice > 1000) {
          discountedPrice = 1000;
      }
      return discountedPrice;
  }

}
