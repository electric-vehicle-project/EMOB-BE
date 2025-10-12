/* EMOB-2025 */
package com.example.emob.model.response.quotation;

import com.example.emob.entity.Dealer;
import com.example.emob.model.request.quotation.QuotationItemRequest;
import com.example.emob.model.response.CustomerResponse;
import com.example.emob.model.response.ElectricVehicleResponse;
import com.example.emob.model.response.PromotionResponse;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.List;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class QuotationResponse {
  List<QuotationItemRequest> items;
  CustomerResponse customer;
  Dealer dealer;
}
