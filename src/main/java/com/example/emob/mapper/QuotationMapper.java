/* EMOB-2025 */
package com.example.emob.mapper;

import com.example.emob.entity.Quotation;
import com.example.emob.entity.QuotationItem;
import com.example.emob.model.request.quotation.QuotationRequest;
import com.example.emob.model.response.quotation.QuotationItemResponse;
import com.example.emob.model.response.quotation.QuotationResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.Collections;
import java.util.Set;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring")
public interface QuotationMapper {
  @Mapping(source = "dealer.id", target = "dealerId")
  @Mapping(source = "account.id", target = "accountId")
  @Mapping(source = "customer.id", target = "customerId")
  @Mapping(source = "status", target = "status")
  QuotationResponse toQuotationResponse(Quotation quotation);

  QuotationItemResponse toQuotationItemResponse(QuotationItem item);

  default Set<QuotationItemResponse> filterActiveItems(Set<QuotationItem> items) {
    if (items == null) return Collections.emptySet();
    return items.stream()
            .filter(item -> !Boolean.TRUE.equals(item.isDeleted()))
            .map(this::toQuotationItemResponse)
            .collect(Collectors.toSet());
  }

  @org.mapstruct.AfterMapping
  default void afterMapping(@org.mapstruct.MappingTarget QuotationResponse response, Quotation quotation) {
    response.setItems(filterActiveItems(quotation.getQuotationItems()));
  }
}
