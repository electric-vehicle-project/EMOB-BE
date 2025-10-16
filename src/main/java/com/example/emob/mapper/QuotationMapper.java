/* EMOB-2025 */
package com.example.emob.mapper;

import com.example.emob.entity.Quotation;
import com.example.emob.model.request.quotation.QuotationRequest;
import com.example.emob.model.response.quotation.QuotationResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface QuotationMapper {
  Quotation toQuotation(QuotationRequest request);

  @Mapping(source = "dealer.id", target = "dealerId")
  @Mapping(source = "account.id", target = "accountId")
  @Mapping(source = "customer.id", target = "customerId")
  QuotationResponse toQuotationResponse(Quotation account);
}
