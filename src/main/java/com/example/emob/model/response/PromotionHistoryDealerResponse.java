package com.example.emob.model.response;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.List;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class PromotionHistoryDealerResponse {
    UUID dealerId;
    String dealerName;
    String country;
    List<PromotionResponse> promotions;
}
