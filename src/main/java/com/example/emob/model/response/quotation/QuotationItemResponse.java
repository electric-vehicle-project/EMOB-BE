package com.example.emob.model.response.quotation;

import com.example.emob.model.response.ElectricVehicleResponse;
import com.example.emob.model.response.PromotionResponse;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class QuotationItemResponse {
    ElectricVehicleResponse vehicle;
    PromotionResponse promotion;
    String color;
    int quantity;
}
