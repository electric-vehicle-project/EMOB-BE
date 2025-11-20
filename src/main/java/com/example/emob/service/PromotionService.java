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
  @Transactional
  @Scheduled(cron = "0 0 0 * * *")
  public void autoUpdatePromotionStatuses() {
    try {
      List<Promotion> promotions = promotionRepository.findAll();
      for (Promotion p : promotions) {
        //                     nếu bị xóa rồi thì bỏ qua
        if (PromotionStatus.INACTIVE.equals(p.getStatus())) {
          continue;
        }
        if (p.getStartDate() == null || p.getEndDate() == null) {
          continue;
        }
        PromotionStatus newStatus =
            PromotionHelper.determinePromotionStatus(p.getStartDate(), p.getEndDate());
        if (newStatus != p.getStatus()) {
          p.setStatus(newStatus);
          promotionRepository.saveAndFlush(p);
        }
      }
    } catch (Exception ex) {
      System.out.println("Lỗi: " + ex.getMessage());
      ex.getStackTrace();
    }
  }

  public Promotion autoUpdatePromotionStatus(Promotion promotion) {
    try {
      //                     nếu bị xóa rồi thì bỏ qua
      if (PromotionStatus.INACTIVE.equals(promotion.getStatus())) {
        return promotion;
      }
      if (promotion.getStartDate() == null || promotion.getEndDate() == null) {
        return promotion;
      }
      PromotionStatus newStatus =
          PromotionHelper.determinePromotionStatus(
              promotion.getStartDate(), promotion.getEndDate());
      if (newStatus != promotion.getStatus()) {
        promotion.setStatus(newStatus);
        promotionRepository.saveAndFlush(promotion);
      }
      return promotion;
    } catch (Exception ex) {
      System.out.println("Lỗi: " + ex.getMessage());
      ex.getStackTrace();
      return promotion;
    }
  }

  @Override
  @Transactional
  @PreAuthorize("hasAnyRole('EVM_STAFF', 'DEALER_STAFF')")
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
        Dealer dealer = AccountUtil.getCurrentUser().getDealer();
        // Tạo Set chứa đúng 1 dealer
        Set<Dealer> dealerSet = Set.of(dealer);
        promotion.setDealers(dealerSet);
      }
      promotion.setVehicles(electricVehicles);
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
      throw new GlobalException(ErrorCode.OTHER, ex.getMessage());
    }
  }

  @Override
  @PreAuthorize("hasAnyRole('EVM_STAFF', 'DEALER_STAFF')")
  public APIResponse<PromotionResponse> updatePromotion(UpdatePromotionRequest request, UUID id) {
    Promotion promotion =
        promotionRepository
            .findById(id)
            .orElseThrow(() -> new GlobalException(ErrorCode.NOT_FOUND));
    try {
      if (promotion.getScope() == PromotionScope.GLOBAL
          || promotion.getScope() == PromotionScope.LOCAL) {
        // tìm xe
        if (request.getElectricVehicleIds() != null && !request.getElectricVehicleIds().isEmpty()) {
          Set<ElectricVehicle> electricVehicles =
              new HashSet<>(electricVehicleRepository.findAllById(request.getElectricVehicleIds()));
          if (promotion.getVehicles().equals(electricVehicles)) {
            throw new GlobalException(
                ErrorCode.DATA_INVALID, "Electric vehicles list is the same as before");
          }
          promotion.getVehicles().addAll(electricVehicles);
        }

        // tìm dealer
        if (request.getDealerIds() != null && !request.getDealerIds().isEmpty()) {
          Set<Dealer> dealers = new HashSet<>(dealerRepository.findAllById(request.getDealerIds()));
          // kiểm tra có trùng danh sách cũ không
          if (promotion.getDealers().equals(dealers)) {
            throw new GlobalException(ErrorCode.DATA_INVALID, "Dealers list is the same as before");
          }
          if (promotion.getScope() == PromotionScope.LOCAL) {
            // --- LOCAL SCOPE chỉ cho phép 1 dealer ---
            Set<UUID> dealerIds = request.getDealerIds();

            if (dealerIds == null || dealerIds.isEmpty() || dealerIds.size() != 1) {
              throw new GlobalException(
                  ErrorCode.DATA_INVALID, "Local promotion must have exactly one dealer");
            }
            // lấy 1 thằng duy nhất
            UUID dealerId = dealerIds.iterator().next();
            Dealer dealer =
                dealerRepository
                    .findById(dealerId)
                    .orElseThrow(() -> new GlobalException(ErrorCode.NOT_FOUND));
            promotion.setDealers(Set.of(dealer)); // chỉ set duy nhất 1 dealer
          } else {
            // global scope có thể nhiều dealer ---
            promotion.getDealers().addAll(dealers);
          }
        }
      }
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
  @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
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
        PromotionStatus status =
            PromotionHelper.determinePromotionStatus(
                promotion.getStartDate(), promotion.getEndDate());
        promotion.setStatus(status);
        promotionRepository.save(promotion);
        PromotionResponse promotionResponse = promotionMapper.toPromotionResponse(promotion);
        return APIResponse.success(promotionResponse, "Create promotion for global successfully");
      } else if (promotion.getScope().equals(PromotionScope.LOCAL)
          && Role.MANAGER.equals(AccountUtil.getCurrentUser().getRole())) {
        updatePromotionDetail(request, promotion);
        PromotionStatus status =
            PromotionHelper.determinePromotionStatus(
                promotion.getStartDate(), promotion.getEndDate());
        promotion.setStatus(status);
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
  @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
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
      Pageable pageable, List<PromotionScope> scopes,List<PromotionStatus> statuses, String keyword) {

    var currentUser = AccountUtil.getCurrentUser();
    var dealer = currentUser.getDealer();

    Page<Promotion> promotions;
    String message;

    // ========== CASE 1: dealer == null (user không thuộc đại lý) ==========
    if (dealer == null) {
      promotions =  promotionRepository.findByScope(PromotionScope.GLOBAL,pageable);
      message ="View all global promotions successfully";
    }
    // ========== CASE 2: dealer != null ==========
    else {
      // Nếu không truyền scope hoặc truyền cả hai → lấy GLOBAL + LOCAL (có liên quan)
      promotions = promotionRepository.findAllAndFilter(scopes, statuses, dealer,keyword, pageable);
        message = "View all promotions for dealer successfully";
    }

    return APIResponse.success(
        pageMapper.toPageResponse(promotions, promotionMapper::toPromotionResponse), message);
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
