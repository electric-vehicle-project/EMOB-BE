/* EMOB-2025 */
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
import com.example.emob.model.response.PromotionHistoryDealerResponse;
import com.example.emob.model.response.PromotionResponse;
import com.example.emob.repository.DealerRepository;
import com.example.emob.repository.ElectricVehicleRepository;
import com.example.emob.repository.PromotionRepository;
import com.example.emob.service.impl.IPromotion;
import com.example.emob.util.AccountUtil;
import com.example.emob.util.PromotionHelper;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class PromotionService implements IPromotion {

  @Autowired DealerRepository dealerRepository;

  @Autowired PromotionRepository promotionRepository;

  @Autowired PromotionMapper promotionMapper;

  @Autowired PageMapper pageMapper;

  @Autowired ElectricVehicleRepository electricVehicleRepository;

  // tự động cập nhật status promotion sau 1p
  @Scheduled(fixedRate = 300000)
  public void autoUpdatePromotionStatus() {
    try {
      List<Promotion> promotions = promotionRepository.findAll();
      for (Promotion p : promotions) {
        //                     nếu bị xóa rồi thì bỏ qua
        if (p.getStatus().equals(PromotionStatus.INACTIVE)) {
          continue;
        }
        PromotionStatus newStatus =
            PromotionHelper.determinePromotionStatus(p.getStartDate(), p.getEndDate());
        if (newStatus != p.getStatus()) {
          p.setStatus(newStatus);
          promotionRepository.save(p);
        }
      }
    } catch (Exception ex) {
      System.out.println("Lỗi: " + ex.getMessage());
    }
  }

  @Override
  @Transactional
  public APIResponse<PromotionResponse> createPromotion(PromotionRequest request) {
    // tim staff
    Account staff = AccountUtil.getCurrentUser();
    // tìm mẫu xe
    Set<ElectricVehicle> electricVehicles =
        new HashSet<>(electricVehicleRepository.findAllById(request.getElectricVehiclesId()));
    if (electricVehicles.isEmpty()) throw new GlobalException(ErrorCode.NOT_FOUND);
    try {
      Promotion promotion = promotionMapper.toPromotion(request);
      // check role
      if (staff.getRole().equals(Role.EVM_STAFF)) {
        promotion.setScope(PromotionScope.GLOBAL);
        // chon dealer nao dc sao promotion
        if (request.getDealerId() != null) {
          Set<Dealer> dealers = new HashSet<>(dealerRepository.findAllById(request.getDealerId()));
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
        Dealer dealer =
            dealerRepository
                .findById(dealerId)
                .orElseThrow(() -> new GlobalException(ErrorCode.NOT_FOUND));
        // Tạo Set chứa đúng 1 dealer
        Set<Dealer> dealerSet = Set.of(dealer);
        promotion.setDealers(dealerSet);
      }
      promotion.setCreateAt(LocalDateTime.now());
      promotion.setCreateBy(staff);
      //            autoUpdatePromotionStatus();
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

  public APIResponse<PromotionResponse> updatePromotion(UpdatePromotionRequest request, UUID id) {
    Promotion promotion =
        promotionRepository
            .findById(id)
            .filter((item) -> (item.getStatus().equals(PromotionStatus.UPCOMING)))
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

  public void updatePromotionDetail(PromotionValueRequest request, Promotion promotion) {
    promotion.setValue(request.getValue());
    promotion.setMinValue(request.getMinPrice());
    promotion.setStartDate(request.getStartDate());
    promotion.setEndDate(request.getEndDate());
    promotion.setType(request.getType());
  }

  @Override
  public APIResponse<PromotionResponse> createValuePromotion(
      UUID id, PromotionValueRequest request) {
    Promotion promotion =
        promotionRepository
            .findById(id)
            .orElseThrow(() -> new GlobalException(ErrorCode.NOT_FOUND));
    try {
      if (promotion.getScope().equals(PromotionScope.GLOBAL)
          && AccountUtil.getCurrentUser().getRole().equals(Role.ADMIN)) {
        updatePromotionDetail(request, promotion);
        promotionRepository.save(promotion);
        PromotionResponse promotionResponse = promotionMapper.toPromotionResponse(promotion);
        return APIResponse.success(promotionResponse, "Create promotion for global successfully");
      } else if (promotion.getScope().equals(PromotionScope.LOCAL)
          && Role.MANAGER.equals(AccountUtil.getCurrentUser().getRole())) {
        updatePromotionDetail(request, promotion);
        promotionRepository.save(promotion);
        PromotionResponse promotionResponse = promotionMapper.toPromotionResponse(promotion);
        return APIResponse.success(promotionResponse, "Create promotion for local successfully");
      } else {
        throw new GlobalException(ErrorCode.UNAUTHORIZED);
      }
    } catch (DataIntegrityViolationException ex) {
      throw new GlobalException(ErrorCode.DATA_INVALID);
    } catch (DataAccessException ex) {
      throw new GlobalException(ErrorCode.DB_ERROR);
    } catch (Exception ex) {
      throw new GlobalException(ErrorCode.OTHER);
    }
  }

  @Override
  public APIResponse<Void> deletePromotion(UUID id) {
    Promotion promotion =
        promotionRepository
            .findById(id)
            .orElseThrow(() -> new GlobalException(ErrorCode.NOT_FOUND));
    try {
      if (promotion.getScope().equals(PromotionScope.GLOBAL)
          && AccountUtil.getCurrentUser().getRole().equals(Role.ADMIN)) {
        promotion.setStatus(PromotionStatus.INACTIVE);
        promotionRepository.save(promotion);
      } else if (promotion.getScope().equals(PromotionScope.LOCAL)
          && Role.MANAGER.equals(AccountUtil.getCurrentUser().getRole())) {
        promotion.setStatus(PromotionStatus.INACTIVE);
        promotionRepository.save(promotion);
      } else {
        throw new GlobalException(ErrorCode.UNAUTHORIZED);
      }
      return APIResponse.success(null, "Delete promotion successfully");
    } catch (DataIntegrityViolationException ex) {
      throw new GlobalException(ErrorCode.DATA_INVALID);
    } catch (DataAccessException ex) {
      throw new GlobalException(ErrorCode.DB_ERROR);
    } catch (Exception ex) {
      throw new GlobalException(ErrorCode.OTHER);
    }
  }

  @Override
  public APIResponse<PageResponse<PromotionResponse>> viewAllPromotions(
      Pageable pageable, PromotionScope scope) {
    if (scope.equals(PromotionScope.LOCAL)) {
      Page<Promotion> promotions = promotionRepository.findByScope(PromotionScope.LOCAL, pageable);
      PageResponse<PromotionResponse> promotionResponsePageResponse =
          pageMapper.toPageResponse(promotions, promotionMapper::toPromotionResponse);
      return APIResponse.success(
          promotionResponsePageResponse, "View All Lccal Promotions Successfully");
    } else if (scope.equals(PromotionScope.GLOBAL)) {
      Page<Promotion> promotions = promotionRepository.findByScope(PromotionScope.GLOBAL, pageable);
      PageResponse<PromotionResponse> promotionResponsePageResponse =
          pageMapper.toPageResponse(promotions, promotionMapper::toPromotionResponse);
      return APIResponse.success(
          promotionResponsePageResponse, "View All Global Promotions Successfully");
    } else {
      Page<Promotion> promotions = promotionRepository.findAll(pageable);
      PageResponse<PromotionResponse> promotionResponsePageResponse =
          pageMapper.toPageResponse(promotions, promotionMapper::toPromotionResponse);
      return APIResponse.success(promotionResponsePageResponse, "View All Promotions Successfully");
    }
  }

  @Override
  public APIResponse<List<PromotionResponse>> viewHistoryDealerPromotion(UUID dealerId) {
    Dealer dealer = dealerRepository.findById(dealerId)
            .orElseThrow(() -> new GlobalException(ErrorCode.NOT_FOUND));
    List<Promotion> promotions = promotionRepository.findAllByDealersId(dealer.getId());
    List<PromotionResponse> responses = promotions
            .stream().map((promotion) ->
                    PromotionResponse.builder().id(promotion.getId())
                      .name(promotion.getName())
                      .description(promotion.getDescription())
                      .type(promotion.getType())
                      .value(promotion.getValue())
                      .minValue(promotion.getMinValue())
                      .startDate(promotion.getStartDate())
                      .endDate(promotion.getEndDate())
                      .scope(promotion.getScope())
                      .status(promotion.getStatus())
                      .createAt(promotion.getCreateAt())
                      .build())
            .toList();
    return APIResponse.success(responses, "View History Dealer Promotion Successfully");
  }

  @Override
  public APIResponse<PromotionResponse> viewPromotion(UUID id) {
    Promotion promotion =
        promotionRepository
            .findById(id)
            .orElseThrow(() -> new GlobalException(ErrorCode.NOT_FOUND));
    PromotionResponse promotionResponse = promotionMapper.toPromotionResponse(promotion);
    return APIResponse.success(promotionResponse, "View Promotion Successfully");
  }
}
