package com.example.emob.service;

import com.example.emob.constant.*;
import com.example.emob.entity.Account;
import com.example.emob.entity.Dealer;
import com.example.emob.entity.Promotion;
import com.example.emob.exception.GlobalException;
import com.example.emob.mapper.PageMapper;
import com.example.emob.mapper.PromotionMapper;
import com.example.emob.model.request.promotion.PromotionRequest;
import com.example.emob.model.request.promotion.UpdatePromotionRequest;
import com.example.emob.model.response.APIResponse;
import com.example.emob.model.response.PageResponse;
import com.example.emob.model.response.PromotionResponse;
import com.example.emob.repository.AccountRepository;
import com.example.emob.repository.DealerRepository;
import com.example.emob.repository.PromotionRepository;
import com.example.emob.service.iml.IPromotion;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;

@Service
public class PromotionService implements IPromotion {

    @Autowired
    DealerRepository dealerRepository;

    @Autowired
    PromotionRepository promotionRepository;

    @Autowired
    PromotionMapper promotionMapper;

    @Autowired
    AccountRepository accountRepository;

    @Autowired
    PageMapper pageMapper;

    @Override
    public APIResponse<PromotionResponse> createPromotion(PromotionRequest request, PromotionScope scope) {
        if (request.getValue() < request.getMinValue()) {
            throw new GlobalException(ErrorCode.DATA_INVALID);
        }
        Account evmStaff = accountRepository.findById(request.getEvmStaffId())
                .filter((item) -> item.getStatus().equals(AccountStatus.ACTIVE))
                .orElseThrow(() -> new GlobalException(ErrorCode.NOT_FOUND));

        try {
            Promotion promotion = promotionMapper.toPromotion(request);
            if (request.getStartDate().isAfter(LocalDateTime.now())) {
                promotion.setStatus(PromotionStatus.UPCOMING);
            } else if (request.getEndDate().isBefore(LocalDateTime.now())) {
                promotion.setStatus(PromotionStatus.EXPIRED);
            } else {
                promotion.setStatus(PromotionStatus.ACTIVE);
            }
            if (scope.equals(PromotionScope.GLOBAL)) {
                // rỗng dealer nghĩa là tất cả là global
                promotion.setDealers(new HashSet<>());
            } else if (scope.equals(PromotionScope.LOCAL)) {
                // phải có ít nhất 1 dealerId trong Set
                if (request.getDealerId() == null) {
                    throw  new GlobalException(ErrorCode.DATA_INVALID);
                }
                Set<Dealer> dealerSet = new HashSet<>(dealerRepository.findAllById(request.getDealerId()));
                if (dealerSet.isEmpty()) {
                    throw new GlobalException(ErrorCode.NOT_FOUND);
                }
                promotion.setDealers(dealerSet);
            }
            promotion.setScope(scope);
            promotion.setCreateAt(LocalDateTime.now());
            promotion.setCreateBy(evmStaff);
            promotionRepository.save(promotion);
            Set<UUID> uuids = promotionMapper.dealersToIds(promotion.getDealers());
            PromotionResponse promotionResponse = promotionMapper.toPromotionResponse(promotion);
            promotionResponse.setManagerId(uuids);
            return APIResponse.success(promotionResponse, "Create promotion for local successfully");
        } catch (DataIntegrityViolationException ex) {
            throw new GlobalException(ErrorCode.DATA_INVALID);
        } catch (DataAccessException ex) {
            throw new GlobalException(ErrorCode.DB_ERROR);
        } catch (ClassCastException ex) {
            throw new GlobalException(ErrorCode.CANNOT_CAST);
        } catch (Exception ex) {
            throw new GlobalException(ErrorCode.OTHER);
        }
    }

    @Override
    public APIResponse<PromotionResponse> updatePromotion(UpdatePromotionRequest request, UUID id) {
        if (request.getStartDate().isAfter(request.getEndDate())) {
            throw new GlobalException(ErrorCode.DATA_INVALID);
        }
        if (request.getValue() < request.getMinValue()) {
            throw new GlobalException(ErrorCode.DATA_INVALID);
        }
        Promotion promotion = promotionRepository.findById(id).filter((item) ->
                        !(item.getStatus().equals(PromotionStatus.INACTIVE)))
                .orElseThrow(() -> new GlobalException(ErrorCode.NOT_FOUND));
        if (request.getStartDate().isAfter(LocalDateTime.now())) {
            promotion.setStatus(PromotionStatus.UPCOMING);
        } else if (request.getEndDate().isBefore(LocalDateTime.now())) {
            promotion.setStatus(PromotionStatus.EXPIRED);
        } else {
            promotion.setStatus(PromotionStatus.ACTIVE);
        }

        try {
            Set<UUID> uuids = promotionMapper.dealersToIds(promotion.getDealers());
            promotionMapper.updatePromotionFromRequest(request, promotion);
            promotion.setUpdateAt(LocalDateTime.now());
            promotionRepository.save(promotion);
            PromotionResponse promotionResponse = promotionMapper.toPromotionResponse(promotion);
            promotionResponse.setManagerId(uuids);
            return APIResponse.success(promotionResponse, "Update promotion successfully");
        } catch (DataIntegrityViolationException ex) {
            throw new GlobalException(ErrorCode.DATA_INVALID);
        } catch (DataAccessException ex) {
            throw new GlobalException(ErrorCode.DB_ERROR);
        } catch (Exception ex) {
            throw new GlobalException(ErrorCode.OTHER);
        }
    }

    @Override
    public APIResponse<PromotionResponse> deletePromotion(UUID id) {
        Promotion promotion = promotionRepository.findById(id)
                .orElseThrow(() -> new GlobalException(ErrorCode.NOT_FOUND));
        try {
            promotion.setStatus(PromotionStatus.INACTIVE);
            promotionRepository.save(promotion);
            return APIResponse.error(200, "Delete promotion successfully");
        } catch (DataIntegrityViolationException ex) {
            throw new GlobalException(ErrorCode.DATA_INVALID);
        } catch (DataAccessException ex) {
            throw new GlobalException(ErrorCode.DB_ERROR);
        } catch (Exception ex) {
            throw new GlobalException(ErrorCode.OTHER);
        }
    }

    @Override
    public APIResponse<PromotionResponse> viewPromotion(UUID id) {
        Promotion promotion = promotionRepository.findById(id)
                .orElseThrow(() -> new GlobalException(ErrorCode.NOT_FOUND));
        Set<UUID> uuids = promotionMapper.dealersToIds(promotion.getDealers());
        PromotionResponse promotionResponse = promotionMapper.toPromotionResponse(promotion);
        promotionResponse.setManagerId(uuids);
        return APIResponse.success(promotionResponse, "View Promotion Successfully");
    }

    @Override
    public APIResponse<PageResponse<PromotionResponse>> viewAllPromotions(Pageable pageable) {
        Page<Promotion> promotions = promotionRepository.findAll(pageable);
        PageResponse<PromotionResponse> promotionResponsePageResponse = pageMapper.toPageResponse(promotions, promotionMapper::toPromotionResponse);
        return APIResponse.success(promotionResponsePageResponse, "View All Promotions Successfully");
    }
}
