/* EMOB-2025 */
package com.example.emob.service;

import com.example.emob.constant.ErrorCode;
import com.example.emob.entity.Dealer;
import com.example.emob.entity.Inventory;
import com.example.emob.entity.SaleOrder;
import com.example.emob.exception.GlobalException;
import com.example.emob.mapper.DealerMapper;
import com.example.emob.mapper.PageMapper;
import com.example.emob.model.request.DealerRequest;
import com.example.emob.model.response.APIResponse;
import com.example.emob.model.response.DealerResponse;
import com.example.emob.model.response.OrderHistoryDealerResponse;
import com.example.emob.model.response.PageResponse;
import com.example.emob.repository.DealerRepository;
import com.example.emob.repository.SaleOrderRepository;
import com.example.emob.service.impl.IDealer;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class DealerService implements IDealer {

  @Autowired DealerRepository dealerRepository;

  @Autowired DealerMapper dealerMapper;

  @Autowired PageMapper pageMapper;

  @Autowired
  SaleOrderRepository orderRepository;

  @Override
  public APIResponse<DealerResponse> create(DealerRequest request) {
    try {
      Dealer dealer = dealerMapper.toDealer(request);
      dealer.setCreatedAt(LocalDateTime.now());

      Inventory inventory = new Inventory();
      dealer.setInventory(inventory);

      dealerRepository.save(dealer);

      DealerResponse response = dealerMapper.toDealerResponse(dealer);
      return APIResponse.success(response, "Created successfully");
    } catch (Exception e) {
      throw new GlobalException(ErrorCode.INVALID_CODE);
    }
  }

  @Override
  public APIResponse<DealerResponse> update(UUID id, DealerRequest request) {
    try {
      Dealer dealer =
          dealerRepository.findById(id).orElseThrow(() -> new GlobalException(ErrorCode.NOT_FOUND));

      Dealer newDealer = dealerMapper.updateDealer(request, dealer);
      dealerRepository.save(newDealer);

      return APIResponse.success(dealerMapper.toDealerResponse(dealer), "Updated successfully");

    } catch (Exception e) {
      throw new GlobalException(ErrorCode.INVALID_CODE);
    }
  }

  @Override
  public APIResponse<DealerResponse> delete(UUID id) {
    try {
      Dealer dealer =
          dealerRepository.findById(id).orElseThrow(() -> new GlobalException(ErrorCode.NOT_FOUND));

      dealer.setDeleted(true);
      dealerRepository.save(dealer);

      DealerResponse response = dealerMapper.toDealerResponse(dealer);
      return APIResponse.success(response, "Dealer deleted successfully");

    } catch (Exception e) {
      throw new GlobalException(ErrorCode.INVALID_CODE);
    }
  }

  @Override
  public APIResponse<DealerResponse> get(UUID id) {
    try {
      Dealer dealer =
          dealerRepository.findById(id).orElseThrow(() -> new GlobalException(ErrorCode.NOT_FOUND));

      return APIResponse.success(dealerMapper.toDealerResponse(dealer));

    } catch (Exception e) {
      throw new GlobalException(ErrorCode.INVALID_CODE);
    }
  }

  @Override
  public APIResponse<PageResponse<DealerResponse>> getAll(Pageable pageable) {
    try {
      Page<Dealer> page = dealerRepository.findAll(pageable);
      PageResponse<DealerResponse> response =
          pageMapper.toPageResponse(page, dealerMapper::toDealerResponse);

      return APIResponse.success(response);
    } catch (Exception e) {
      throw new GlobalException(ErrorCode.INVALID_CODE);
    }
  }

  @Override
  public APIResponse<List<OrderHistoryDealerResponse>> viewOrderHistory(UUID id) {
    Dealer dealer = dealerRepository.findById(id)
            .orElseThrow(() -> new GlobalException(ErrorCode.NOT_FOUND));
    List<SaleOrder> orders = orderRepository.findAllByDealerId(dealer.getId());

    List<OrderHistoryDealerResponse> responses = orders.stream()
            .map(item ->
                    OrderHistoryDealerResponse.builder()
                            .orderId(item.getId())
                            .orderDate(item.getCreateAt())
                            .orderStatus(item.getOrderStatus())
                            .paymentStatus(item.getPaymentStatus())
                            .price(item.getSalePrice())
                            .contractNumber(item.getContract().getContractNumber())
                            .signDate(item.getContract().getSignDate())
                            .build()
                    ).toList();
    return APIResponse.success(responses, "View Order History Dealer Successfully");
  }
}
/*                            .deliveryStatus(item.getContract().getDelivery().getStatus()) */
