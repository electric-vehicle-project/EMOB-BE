/* EMOB-2025 */
package com.example.emob.service;

import com.example.emob.constant.ErrorCode;
import com.example.emob.constant.PromotionStatus;
import com.example.emob.entity.*;
import com.example.emob.exception.GlobalException;
import com.example.emob.mapper.QuotationMapper;
import com.example.emob.model.request.quotation.QuotationItemRequest;
import com.example.emob.model.request.quotation.QuotationRequest;
import com.example.emob.model.response.APIResponse;
import com.example.emob.model.response.PageResponse;
import com.example.emob.model.response.QuotationResponse;
import com.example.emob.repository.*;
import com.example.emob.service.impl.IQuotation;

import java.util.List;
import java.util.UUID;

import com.example.emob.util.PromotionHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class QuotationService implements IQuotation {
  @Autowired private QuotationRepository quotationRepository;
  @Autowired private QuotationMapper quotationMapper;
  @Autowired private ElectricVehicleRepository electricVehicleRepository;
  @Autowired private PromotionRepository promotionRepository;
  @Autowired private CustomerRepository customerRepository;
  @Autowired private DealerRepository dealerRepository;

  @Override
  public APIResponse<QuotationResponse> create(QuotationRequest request) {
    try{
      double price = 0;
      //báo giá cho customer
      if(request.getCustomerId() != null){
        Customer customer = customerRepository.findById(request.getCustomerId()).orElseThrow(() -> new GlobalException(ErrorCode.NOT_FOUND, "Customer not found"));
      }
      //báo giá cho dealer
      Dealer dealer = dealerRepository.findById(request.getDealerId()).orElseThrow(() -> new GlobalException(ErrorCode.NOT_FOUND, "Dealer not found"));



      for(QuotationItemRequest itemRequest : request.getItems()){
          QuotationItem items = createQuotationItem(itemRequest);
          if(dealer == null){
            //báo giá cho customer - giá bán
            price = items.getVehicle().getRetailPrice();
          }else{
            //báo giá cho dealer - giá nhập
            price = items.getVehicle().getImportPrice();
          }
        //check khuyến mãi
        Promotion promotion = promotionRepository.findById(itemRequest.getPromotionId()) .orElseThrow(() -> new GlobalException(ErrorCode.NOT_FOUND, "promotion not found"));
        //check promotion valid
        PromotionHelper.checkPromotionValid(promotion);
        //set discount




      }


      return null;
    }catch (Exception e){
      throw new GlobalException(ErrorCode.INTERNAL_SERVER_ERROR, e.getMessage());
    }

  }

  @Override
  public APIResponse<QuotationResponse> update(UUID id, QuotationRequest request) {
    return null;
  }

  @Override
  public APIResponse<QuotationResponse> delete(UUID id) {
    return null;
  }

  @Override
  public APIResponse<QuotationResponse> get(UUID id) {
    return null;
  }

  @Override
  public APIResponse<PageResponse<QuotationResponse>> getAll(Pageable pageable) {
    return null;
  }


  private QuotationItem createQuotationItem(QuotationItemRequest request) {
    ElectricVehicle vehicle = electricVehicleRepository
            .findById(request.getVehicleId())
            .orElseThrow(() -> new GlobalException(ErrorCode.NOT_FOUND, "Vehicle not found"));
    return QuotationItem.builder()
            .color(request.getColor())
            .quantity(request.getQuantity())
            .vehicle(vehicle)
            .totalPrice(request.getQuantity() * vehicle.getRetailPrice())
            .build();
  }

}
