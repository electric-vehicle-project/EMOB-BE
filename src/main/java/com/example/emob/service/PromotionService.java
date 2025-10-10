package com.example.emob.service;

import com.example.emob.constant.ErrorCode;
import com.example.emob.constant.PromotionScope;
import com.example.emob.constant.PromotionStatus;
import com.example.emob.constant.Role;
import com.example.emob.entity.Account;
import com.example.emob.entity.Dealer;
import com.example.emob.entity.ElectricVehicle;
import com.example.emob.entity.Promotion;
import com.example.emob.exception.GlobalException;
import com.example.emob.mapper.PageMapper;
import com.example.emob.mapper.PromotionMapper;
import com.example.emob.model.request.promotion.PromotionRequest;
import com.example.emob.model.request.promotion.PromotionValueRequest;
import com.example.emob.model.request.promotion.UpdatePromotionRequest;
import com.example.emob.model.response.APIResponse;
import com.example.emob.model.response.PageResponse;
import com.example.emob.model.response.PromotionResponse;
import com.example.emob.repository.AccountRepository;
import com.example.emob.repository.DealerRepository;
import com.example.emob.repository.ElectricVehicleRepository;
import com.example.emob.repository.PromotionRepository;
import com.example.emob.service.iml.IPromotion;
import com.example.emob.util.AccountUtil;
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
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

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

    @Autowired
    ElectricVehicleRepository electricVehicleRepository;


    @Override
    @Transactional
    @PreAuthorize("hasRole('EVM_STAFF') or hasRole('DEALER_STAFF')")
    public APIResponse<PromotionResponse> createPromotion(PromotionRequest request) {
        //tim staff
        Account staff = AccountUtil.getCurrentUser();
        System.out.println("vào chưa: " + staff.getId());

        // tìm mẫu xe
        System.out.println("re: " + request.getElectricVehiclesId());
        Set<ElectricVehicle> electricVehicles = new HashSet<>(electricVehicleRepository
                .findAllById(request.getElectricVehiclesId()));
        System.out.println("vào chưa: " + electricVehicles);
        if (electricVehicles.isEmpty()) throw new GlobalException(ErrorCode.NOT_FOUND);
        try {
            Promotion promotion = promotionMapper.toPromotion(request);
            // check role
            if (staff.getRole().equals(Role.EVM_STAFF)) {
                promotion.setScope(PromotionScope.GLOBAL);
                // chon dealer nao dc sao promotion
                if (request.getDealerId() != null) {
                    Set<Dealer> dealers = new HashSet<>(dealerRepository
                            .findAllById(request.getDealerId()));
                    promotion.setDealers(dealers);
                } else {
                    promotion.setDealers(new HashSet<>(dealerRepository.findAll()));
                }
            } else if (staff.getRole().equals(Role.DEALER_STAFF)) {
                promotion.setScope(PromotionScope.LOCAL);
                Set<UUID> dealerIds = request.getDealerId();
                // phải có duy nhất 1 dealerId trong Set
                if (dealerIds == null || dealerIds.isEmpty()) {
                    throw new GlobalException(ErrorCode.DATA_INVALID);
                }
                // Phải có duy nhất 1 dealerId
                if (dealerIds.size() != 1) {
                    throw new GlobalException(ErrorCode.DATA_INVALID);
                }
                // Lấy ID duy nhất
                UUID dealerId = dealerIds.iterator().next();
                // Truy vấn dealer
                Dealer dealer = dealerRepository.findById(dealerId)
                        .orElseThrow(() -> new GlobalException(ErrorCode.NOT_FOUND));
                // Tạo Set chứa đúng 1 dealer
                Set<Dealer> dealerSet = Set.of(dealer);
                promotion.setDealers(dealerSet);
            }
            // check status
            PromotionStatus status = PromotionHelper.determinePromotionStatus(promotion.getStartDate(),
                    promotion.getEndDate());
            if (status == null) {
                throw new NullPointerException();
            }
            promotion.setStatus(status);
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
    @PreAuthorize("hasRole('EVM_STAFF') or hasRole('DEALER_STAFF')")
    public APIResponse<PromotionResponse> updatePromotion(UpdatePromotionRequest request, UUID id) {
        Promotion promotion = promotionRepository.findById(id).filter((item) ->
                        (item.getStatus().equals(PromotionStatus.ACTIVE)))
                .orElseThrow(() -> new GlobalException(ErrorCode.NOT_FOUND));
        try {
            // mapper
            promotion.setUpdateAt(LocalDateTime.now());
            promotion.setName(request.getName());
            promotion.setDescription(request.getDescription());
            promotionRepository.save(promotion);
            PromotionResponse promotionResponse = promotionMapper.toPromotionResponse(promotion);
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
    public APIResponse<PromotionResponse> createValuePromotion(UUID id, PromotionValueRequest request) {
        Promotion promotion = promotionRepository.findById(id)
                .orElseThrow(() -> new GlobalException(ErrorCode.NOT_FOUND));
        try {
            promotion.setValue(request.getValue());
            promotion.setMinValue(request.getMinPrice());
            promotion.setStartDate(request.getStartDate());
            promotion.setEndDate(request.getEndDate());
            promotion.setType(request.getType());
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
    @PreAuthorize("hasRole('MANAGER')")
    public APIResponse<PageResponse<PromotionResponse>> viewAllLocalPromotions(Pageable pageable) {
        Page<Promotion> promotions = promotionRepository.findByScope(PromotionScope.LOCAL, pageable);
        PageResponse<PromotionResponse> promotionResponsePageResponse = pageMapper.toPageResponse(promotions, promotionMapper::toPromotionResponse);
        return APIResponse.success(promotionResponsePageResponse, "View All Promotions Successfully");
    }

    @Override
    @PreAuthorize("hasRole('ADMIN')")
    public APIResponse<PageResponse<PromotionResponse>> viewAllGlobalPromotions(Pageable pageable) {
        Page<Promotion> promotions = promotionRepository.findByScope(PromotionScope.GLOBAL, pageable);
        PageResponse<PromotionResponse> promotionResponsePageResponse = pageMapper.toPageResponse(promotions, promotionMapper::toPromotionResponse);
        return APIResponse.success(promotionResponsePageResponse, "View All Global Promotions Successfully");
    }

    @Override
    public APIResponse<PromotionResponse> viewPromotion(UUID id) {
        Promotion promotion = promotionRepository.findById(id)
                .orElseThrow(() -> new GlobalException(ErrorCode.NOT_FOUND));
        PromotionResponse promotionResponse = promotionMapper.toPromotionResponse(promotion);
        return APIResponse.success(promotionResponse, "View Promotion Successfully");
    }
}
