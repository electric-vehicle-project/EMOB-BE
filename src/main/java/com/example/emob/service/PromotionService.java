/* EMOB-2025 */
package com.example.emob.service;

import com.example.emob.constant.*;
import com.example.emob.entity.*;
import com.example.emob.exception.GlobalException;
import com.example.emob.mapper.PageMapper;
import com.example.emob.mapper.PromotionMapper;
import com.example.emob.model.request.promotion.PromotionRequest;
import com.example.emob.model.request.promotion.PromotionValueRequest;
import com.example.emob.model.request.promotion.UpdatePromotionRequest;
import com.example.emob.model.response.APIResponse;
import com.example.emob.model.response.PageResponse;
import com.example.emob.model.response.PromotionResponse;
import com.example.emob.repository.*;
import com.example.emob.service.iml.IPromotion;
import com.example.emob.util.PromotionHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;

@Service
public class PromotionService implements IPromotion {

    @Autowired DealerRepository dealerRepository;

    @Autowired PromotionRepository promotionRepository;

    @Autowired PromotionMapper promotionMapper;


    @Autowired
    AccountRepository accountRepository;

    @Autowired
    PageMapper pageMapper;

    @Autowired
    ElectricVehicleRepository electricVehicleRepository;


    @Override
    @Transactional
    @PreAuthorize("hasRole('EVM_STAFF') or hasRole('DEALER_STAFF')")
    public APIResponse<PromotionResponse> createPromotion(PromotionRequest request) {
        //tim staff
        Account staff = accountRepository.findById(request.getStaffId())
                .filter((item) -> item.getStatus().equals(AccountStatus.ACTIVE))
                .orElseThrow(() -> new GlobalException(ErrorCode.NOT_FOUND));
        System.out.println("tìm thấy: " + staff.getId());

        Set<ElectricVehicle> electricVehicles = new HashSet<>(electricVehicleRepository
                .findAllById(request.getElectricVehiclesId()));
        if (electricVehicles.isEmpty()) throw new GlobalException(ErrorCode.NOT_FOUND);
        try {
            //tim pormotion
            Promotion promotion = promotionMapper.toPromotion(request);
            // check role
            if (staff.getRole().equals(Role.EVM_STAFF)) {
                promotion.setScope(PromotionScope.GLOBAL);
                // chon dealer nao dc sao promotion
                if(request.getDealerId() !=null){

                    Set<Dealer> dealers = new HashSet<>(dealerRepository
                            .findAllById(request.getDealerId()));
                    promotion.setDealers(dealers);
                }else{
                    promotion.setDealers(new HashSet<>(dealerRepository.findAll()));
                }
            } else if (staff.getRole().equals(Role.DEALER_STAFF)) {
                promotion.setScope(PromotionScope.LOCAL);
                Set<UUID> dealerIds = request.getDealerId();
                // phải có duy nhất 1 dealerId trong Set
                if (dealerIds == null || dealerIds.isEmpty()) {
                    throw new GlobalException(ErrorCode.DATA_INVALID);
                }
                // ✅ Phải có duy nhất 1 dealerId
                if (dealerIds.size() != 1) {
                    throw new GlobalException(ErrorCode.DATA_INVALID);
                }
                // ✅ Lấy ID duy nhất
                UUID dealerId = dealerIds.iterator().next();
                // ✅ Truy vấn dealer
                Dealer dealer = dealerRepository.findById(dealerId)
                        .orElseThrow(() -> new GlobalException(ErrorCode.NOT_FOUND));
                // ✅ Tạo Set chứa đúng 1 dealer
                Set<Dealer> dealerSet = Set.of(dealer);
                promotion.setDealers(dealerSet);
                // gán khuyến mãi cho các mẫu xe
            }
            promotion.setCreateAt(LocalDateTime.now());
            promotion.setCreateBy(staff);
            promotionRepository.save(promotion);
            PromotionResponse promotionResponse = promotionMapper.toPromotionResponse(promotion);
            return APIResponse.success(promotionResponse, "Create promotion for local successfully");
        } catch (DataIntegrityViolationException ex) {
            throw new GlobalException(ErrorCode.DATA_INVALID);
        } catch (DataAccessException ex) {
            throw new GlobalException(ErrorCode.DB_ERROR);
        } catch (Exception ex) {
            throw new GlobalException(ErrorCode.OTHER);
        }
    }
    @Override
    public APIResponse<PromotionResponse> createValuePromotion(UUID id, PromotionValueRequest request) {
        Promotion promotion = promotionRepository.findById(id).orElseThrow(() -> new GlobalException(ErrorCode.NOT_FOUND));
        promotion.setValue(request.getValue());
        promotion.setMinValue(request.getMinPrice());
        promotion.setStartDate(request.getStartDate());
        promotion.setEndDate(request.getEndDate());
        promotion.setType(request.getType());
        promotionRepository.save(promotion);
        PromotionResponse promotionResponse = promotionMapper.toPromotionResponse(promotion);
        return APIResponse.success(promotionResponse, "Create promotion for local successfully");
    }


//    @Override
//    public APIResponse<PromotionResponse> updatePromotion(UpdatePromotionRequest request, UUID id) {
//        if (request.getStartDate().isAfter(request.getEndDate())) {
//            throw new GlobalException(ErrorCode.DATA_INVALID);
//        }
//        if (request.getValue() < request.getMinValue()) {
//            throw new GlobalException(ErrorCode.DATA_INVALID);
//        }
//        Promotion promotion = promotionRepository.findById(id).filter((item) ->
//                        !(item.getStatus().equals(PromotionStatus.INACTIVE)))
//                .orElseThrow(() -> new GlobalException(ErrorCode.NOT_FOUND));
//        if (request.getStartDate().isAfter(LocalDateTime.now())) {
//            promotion.setStatus(PromotionStatus.UPCOMING);
//        } else if (request.getEndDate().isBefore(LocalDateTime.now())) {
//            promotion.setStatus(PromotionStatus.EXPIRED);
//        } else {
//            promotion.setStatus(PromotionStatus.ACTIVE);
//        }
//
//        try {
//            Set<UUID> vehiclesToIds = promotionMapper.vehiclesToIds(promotion.getVehicles());
//            Set<UUID> dealersToIds = promotionMapper.dealersToIds(promotion.getDealers());
//            promotionMapper.updatePromotionFromRequest(request, promotion);
//            promotion.setUpdateAt(LocalDateTime.now());
//            promotionRepository.save(promotion);
//            PromotionResponse promotionResponse = promotionMapper.toPromotionResponse(promotion);
//            promotionResponse.setDealerId(dealersToIds);
//            promotionResponse.setVehicleId(vehiclesToIds);
//            return APIResponse.success(promotionResponse, "Update promotion successfully");
//        } catch (DataIntegrityViolationException ex) {
//            throw new GlobalException(ErrorCode.DATA_INVALID);
//        } catch (DataAccessException ex) {
//            throw new GlobalException(ErrorCode.DB_ERROR);
//        } catch (Exception ex) {
//            throw new GlobalException(ErrorCode.OTHER);
//        }
//    }
//
//    @Override
//    public APIResponse<PromotionResponse> deletePromotion(UUID id) {
//        Promotion promotion = promotionRepository.findById(id)
//                .orElseThrow(() -> new GlobalException(ErrorCode.NOT_FOUND));
//        try {
//            promotion.setStatus(PromotionStatus.INACTIVE);
//            promotionRepository.save(promotion);
//            return APIResponse.error(200, "Delete promotion successfully");
//        } catch (DataIntegrityViolationException ex) {
//            throw new GlobalException(ErrorCode.DATA_INVALID);
//        } catch (DataAccessException ex) {
//            throw new GlobalException(ErrorCode.DB_ERROR);
//        } catch (Exception ex) {
//            throw new GlobalException(ErrorCode.OTHER);
//        }
//    }
//
//    @Override
//    public APIResponse<PromotionResponse> viewPromotion(UUID id) {
//        Promotion promotion = promotionRepository.findById(id)
//                .orElseThrow(() -> new GlobalException(ErrorCode.NOT_FOUND));
//        Set<UUID> uuids = promotionMapper.dealersToIds(promotion.getDealers());
//        Set<UUID> vehiclesToIds = promotionMapper.vehiclesToIds(promotion.getVehicles());
//        PromotionResponse promotionResponse = promotionMapper.toPromotionResponse(promotion);
//        promotionResponse.setDealerId(uuids);
//        promotionResponse.setVehicleId(vehiclesToIds);
//        return APIResponse.success(promotionResponse, "View Promotion Successfully");
//    }
//
//    @Override
//    public APIResponse<PageResponse<PromotionResponse>> viewAllPromotions(Pageable pageable) {
//        Page<Promotion> promotions = promotionRepository.findAll(pageable);
//        PageResponse<PromotionResponse> promotionResponsePageResponse = pageMapper.toPageResponse(promotions, promotionMapper::toPromotionResponse);
//        return APIResponse.success(promotionResponsePageResponse, "View All Promotions Successfully");
//    }
//
//    @Override
//    public APIResponse<PageResponse<PromotionResponse>> viewAllGlobalPromotions(Pageable pageable) {
//        Page<Promotion> promotions = promotionRepository.findByScope(PromotionScope.GLOBAL, pageable);
//        PageResponse<PromotionResponse> promotionResponsePageResponse = pageMapper.toPageResponse(promotions, promotionMapper::toPromotionResponse);
//        return APIResponse.success(promotionResponsePageResponse, "View All Global Promotions Successfully");
//    }




}
