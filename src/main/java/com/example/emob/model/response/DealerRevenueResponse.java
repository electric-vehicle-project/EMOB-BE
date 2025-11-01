package com.example.emob.model.response;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class DealerRevenueResponse {
    List<DealerRevenueItemResponse> items;
    Long totalDealer;
}
