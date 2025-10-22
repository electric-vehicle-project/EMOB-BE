package com.example.emob.mapper;

import com.example.emob.entity.Quotation;
import com.example.emob.entity.QuotationItem;
import com.example.emob.entity.SaleOrder;
import com.example.emob.entity.SaleOrderItem;
import com.example.emob.model.response.SaleOrder.SaleOrderItemResponse;
import com.example.emob.model.response.SaleOrder.SaleOrderResponse;
import com.example.emob.model.response.quotation.QuotationItemResponse;
import com.example.emob.model.response.quotation.QuotationResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.Collections;
import java.util.Set;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring")
public interface SaleOrderMapper {
    @Mapping(source = "dealer.id", target = "dealerId")
    @Mapping(source = "account.id", target = "accountId")
    @Mapping(source = "customer.id", target = "customerId")
    @Mapping(source = "status", target = "status")
    SaleOrderResponse toSaleOrderResponse(SaleOrder saleOrder);

    SaleOrderItemResponse toSaleOrderItemResponse(SaleOrderItem item);

    default Set<SaleOrderItemResponse> filterActiveItems(Set<SaleOrderItem> items) {
        if (items == null) return Collections.emptySet();
        return items.stream()
                .map(this::toSaleOrderItemResponse)
                .collect(Collectors.toSet());
    }

    @org.mapstruct.AfterMapping
    default void afterMapping(@org.mapstruct.MappingTarget SaleOrderResponse response, SaleOrder saleOrder) {
        response.setItems(filterActiveItems(saleOrder.getSaleOrderItems()));
    }
}
