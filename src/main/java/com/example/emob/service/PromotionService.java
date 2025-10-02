package com.example.emob.service;

import com.example.emob.constant.ErrorCode;
import com.example.emob.entity.Dealer;
import com.example.emob.entity.Promotion;
import com.example.emob.exception.GlobalException;
import com.example.emob.mapper.PromotionMapper;
import com.example.emob.model.request.promotion.PromotionRequest;
import com.example.emob.model.response.APIResponse;
import com.example.emob.model.response.PromotionResponse;
import com.example.emob.repository.DealerRepository;
import com.example.emob.repository.PromotionRepository;
import com.example.emob.service.iml.IPromotion;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class PromotionService implements IPromotion {

    @Autowired
    DealerRepository dealerRepository;

    @Autowired
    PromotionRepository promotionRepository;

    @Autowired
    PromotionMapper promotionMapper;
    @Override
    public APIResponse<PromotionResponse> createPromotion(PromotionRequest request) {
        Dealer dealer = dealerRepository.findById(request.getDealerId())
                .filter(person -> !person.isDeleted())
                .orElseThrow(() -> new GlobalException(ErrorCode.NOT_FOUND));
        try {
            Promotion promotion = promotionMapper.toPromotion(request);
            promotion.setCreateAt(LocalDateTime.now());
            promotion.setDealer(dealer);
            promotionRepository.save(promotion);
            PromotionResponse promotionResponse = promotionMapper.toPromotionResponse(promotion);
            return APIResponse.success(promotionResponse, "Create promotion successfully");
        } catch (DataIntegrityViolationException ex) {
            throw new GlobalException(ErrorCode.DATA_INVALID);
        } catch (DataAccessException ex) {
            throw new GlobalException(ErrorCode.DB_ERROR);
        } catch (Exception ex) {
            throw new GlobalException(ErrorCode.OTHER);
        }
    }
}
