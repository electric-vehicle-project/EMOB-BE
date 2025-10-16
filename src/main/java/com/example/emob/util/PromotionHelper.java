/* EMOB-2025 */
package com.example.emob.util;

import com.example.emob.constant.PromotionStatus;
import com.example.emob.mapper.PromotionMapper;
import com.example.emob.repository.PromotionRepository;
import java.time.LocalDateTime;
import org.springframework.beans.factory.annotation.Autowired;

public class PromotionHelper {
  @Autowired private static PromotionMapper promotionMapper;

  @Autowired private static PromotionRepository promotionRepository;

  public static PromotionStatus determinePromotionStatus(
      LocalDateTime startDate, LocalDateTime endDate) {
    if (startDate.isAfter(LocalDateTime.now())) return PromotionStatus.UPCOMING;
    if (endDate.isBefore(LocalDateTime.now())) return PromotionStatus.EXPIRED;
    return PromotionStatus.ACTIVE;
  }
}
