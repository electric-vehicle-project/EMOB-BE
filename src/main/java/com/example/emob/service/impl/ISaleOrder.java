package com.example.emob.service.impl;

import com.example.emob.constant.PaymentStatus;
import com.example.emob.entity.Quotation;
import com.example.emob.model.request.SaleOrderItemRequest;
import com.example.emob.model.response.APIResponse;
import com.example.emob.model.response.PageResponse;
import com.example.emob.model.response.SaleOrder.SaleOrderResponse;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.UUID;

public interface ISaleOrder {
    APIResponse<SaleOrderResponse> createSaleOrderFromQuotation(
            Quotation quotation,
            List<SaleOrderItemRequest> itemRequests,
            PaymentStatus paymentStatus
    );
    APIResponse<SaleOrderResponse> deleteSaleOrderById(UUID saleOrderId);

    APIResponse<SaleOrderResponse> getSaleOrderById(UUID saleOrderId);

    APIResponse<PageResponse<SaleOrderResponse>> getAllSaleOrders(Pageable pageable);
}