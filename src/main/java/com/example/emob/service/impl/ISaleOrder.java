/* EMOB-2025 */
package com.example.emob.service.impl;

import com.example.emob.constant.OrderStatus;
import com.example.emob.constant.PaymentStatus;
import com.example.emob.entity.Quotation;
import com.example.emob.entity.VehicleRequest;
import com.example.emob.model.request.SaleOrderItemRequest;
import com.example.emob.model.request.installment.InstallmentRequest;
import com.example.emob.model.response.APIResponse;
import com.example.emob.model.response.PageResponse;
import com.example.emob.model.response.SaleOrder.SaleOrderResponse;
import java.util.List;
import java.util.UUID;
import org.springframework.data.domain.Pageable;

public interface ISaleOrder {
  APIResponse<SaleOrderResponse> createSaleOrderFromQuotation(
      Quotation quotation, List<SaleOrderItemRequest> itemRequests, PaymentStatus paymentStatus);

  APIResponse<SaleOrderResponse> deleteSaleOrderById(UUID saleOrderId);

  APIResponse<SaleOrderResponse> getSaleOrderById(UUID saleOrderId);

  APIResponse<PageResponse<SaleOrderResponse>> getAllSaleOrdersOfCurrentDealer(
      List<OrderStatus> statuses, Pageable pageable);

  APIResponse<SaleOrderResponse> createSaleOrderFromVehicleRequest(
      VehicleRequest vehicleRequest, PaymentStatus paymentStatus);

  APIResponse<SaleOrderResponse> completeSaleOrderById(InstallmentRequest request);

  APIResponse<PageResponse<SaleOrderResponse>> getAllSaleOrdersOfDealer(
      List<OrderStatus> statuses, Pageable pageable);

  APIResponse<PageResponse<SaleOrderResponse>> getAllSaleOrdersOfCurrentCustomer(
      UUID customerid, List<OrderStatus> statuses, Pageable pageable);
}
