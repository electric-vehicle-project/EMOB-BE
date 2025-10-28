/* EMOB-2025 */
package com.example.emob.service.impl;

import com.example.emob.constant.DeliveryStatus;
import com.example.emob.model.request.delivery.DeliveryRequest;
import com.example.emob.model.response.APIResponse;
import com.example.emob.model.response.DeliveryResponse;
import com.example.emob.model.response.PageResponse;
import java.util.List;
import java.util.UUID;
import org.springframework.data.domain.Pageable;

public interface IDelivery {

  APIResponse<DeliveryResponse> createDeliveryToDealer(DeliveryRequest request);

  APIResponse<DeliveryResponse> createDeliveryToCustomer(DeliveryRequest request);

  APIResponse<DeliveryResponse> getDelivery(UUID id);

  APIResponse<Void> deleteDelivery(UUID id);

  APIResponse<PageResponse<DeliveryResponse>> getAllDeliveriesOfDealers(
      List<DeliveryStatus> statuses, Pageable pageable);

  APIResponse<PageResponse<DeliveryResponse>> getAllDeliveriesOfCurrentCustomer(
      UUID customerId, List<DeliveryStatus> statuses, Pageable pageable);

  APIResponse<PageResponse<DeliveryResponse>> getAllDeliveriesOfCurrentDealer(
      List<DeliveryStatus> statuses, Pageable pageable);

  APIResponse<PageResponse<DeliveryResponse>> getAllDeliveriesByCustomer(
      List<DeliveryStatus> statuses, Pageable pageable);

  APIResponse<DeliveryResponse> getDeliveryById(UUID deliveryId);

  APIResponse<DeliveryResponse> completeDelivery(UUID deliveryId);
}
