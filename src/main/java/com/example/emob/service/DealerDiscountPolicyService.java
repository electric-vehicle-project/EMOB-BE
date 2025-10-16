/* EMOB-2025 */
package com.example.emob.service;

import com.example.emob.constant.DiscountPolicyStatus;
import com.example.emob.constant.ErrorCode;
import com.example.emob.entity.Dealer;
import com.example.emob.entity.DealerDiscountPolicy;
import com.example.emob.entity.ElectricVehicle;
import com.example.emob.exception.GlobalException;
import com.example.emob.mapper.DealerDiscountPolicyMapper;
import com.example.emob.mapper.PageMapper;
import com.example.emob.model.request.dealerDiscountPolicy.DealerDiscountPolicyBulkDeleteRequest;
import com.example.emob.model.request.dealerDiscountPolicy.DealerDiscountPolicyBulkRequest;
import com.example.emob.model.request.dealerDiscountPolicy.DealerDiscountPolicyRequest;
import com.example.emob.model.response.APIResponse;
import com.example.emob.model.response.DealerDiscountPolicyResponse;
import com.example.emob.model.response.PageResponse;
import com.example.emob.repository.DealerDiscountPolicyRepository;
import com.example.emob.repository.DealerRepository;
import com.example.emob.repository.ElectricVehicleRepository;
import com.example.emob.service.impl.IDealerDiscountPolicy;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class DealerDiscountPolicyService implements IDealerDiscountPolicy {
  @Autowired private DealerRepository dealerRepository;

  @Autowired private ElectricVehicleRepository electricVehicleRepository;

  @Autowired private DealerDiscountPolicyRepository dealerDiscountPolicyRepository;

  @Autowired private DealerDiscountPolicyMapper dealerDiscountPolicyMapper;

  @Autowired PageMapper pageMapper;

  @Override
  public APIResponse<List<DealerDiscountPolicyResponse>> bulkCreate(
      DealerDiscountPolicyBulkRequest request) {
    List<DealerDiscountPolicy> policies = new ArrayList<>();

    List<Dealer> dealers = dealerRepository.findAllById(request.getDealerIds());
    if (dealers.size() != request.getDealerIds().size()) {
      throw new GlobalException(ErrorCode.NOT_FOUND, "One or more dealers not found");
    }

    List<ElectricVehicle> models =
        electricVehicleRepository.findAllById(request.getVehicleModelIds());
    if (models.size() != request.getVehicleModelIds().size()) {
      throw new GlobalException(ErrorCode.NOT_FOUND, "One or more vehicle models not found");
    }

    for (Dealer dealer : dealers) {
      for (ElectricVehicle model : models) {
        DealerDiscountPolicy policy = new DealerDiscountPolicy();
        policy.setDealer(dealer);
        policy.setVehicle(model);
        policy.setCustomMultiplier(request.getCustomMultiplier());
        policy.setFinalPrice(request.getFinalPrice());
        policy.setEffectiveDate(request.getEffectiveDate());
        policy.setExpiryDate(request.getExpiredDate());

        policies.add(policy);
      }
    }

    // Save tất cả policy cùng lúc
    List<DealerDiscountPolicy> savedPolicies = dealerDiscountPolicyRepository.saveAll(policies);

    // Map sang response
    List<DealerDiscountPolicyResponse> responses =
        savedPolicies.stream()
            .map(dealerDiscountPolicyMapper::toResponse)
            .collect(Collectors.toList());

    return APIResponse.success(responses, "Bulk dealer discount policies created successfully");
  }

  @Override
  public APIResponse<List<DealerDiscountPolicyResponse>> bulkUpdate(
      DealerDiscountPolicyBulkRequest request) {
    // Lấy tất cả policy cần update
    List<DealerDiscountPolicy> policies =
        dealerDiscountPolicyRepository.findAllByDealerIdInAndVehicleIdIn(
            request.getDealerIds(), request.getVehicleModelIds());

    if (policies.isEmpty()) {
      throw new GlobalException(ErrorCode.NOT_FOUND, "No matching DealerDiscountPolicy found");
    }

    // Cập nhật các trường
    for (DealerDiscountPolicy policy : policies) {
      // Cập nhật các trường nếu không null
      if (request.getCustomMultiplier() != null) {
        policy.setCustomMultiplier(request.getCustomMultiplier());
      }
      if (request.getFinalPrice() != null) {
        policy.setFinalPrice(request.getFinalPrice());
      }
      if (request.getEffectiveDate() != null) {
        policy.setEffectiveDate(request.getEffectiveDate());
      }
      if (request.getExpiredDate() != null) {
        policy.setExpiryDate(request.getExpiredDate());
      }
      // Cập nhật thời gian
      policy.setUpdateAt(LocalDateTime.now());
    }

    // Save tất cả cùng lúc
    List<DealerDiscountPolicy> updatedPolicies = dealerDiscountPolicyRepository.saveAll(policies);

    // Map sang response
    List<DealerDiscountPolicyResponse> responses =
        updatedPolicies.stream()
            .map(dealerDiscountPolicyMapper::toResponse)
            .collect(Collectors.toList());
    return APIResponse.success(responses, "Bulk dealer discount policies updated successfully");
  }

  @Override
  public APIResponse<List<DealerDiscountPolicyResponse>> bulkDelete(
      DealerDiscountPolicyBulkDeleteRequest request) {
    // Lấy tất cả policy cần update
    List<DealerDiscountPolicy> policies =
        dealerDiscountPolicyRepository.findAllByDealerIdInAndVehicleIdIn(
            request.getDealerIds(), request.getVehicleModelIds());

    if (policies.isEmpty()) {
      throw new GlobalException(ErrorCode.NOT_FOUND, "No matching DealerDiscountPolicy found");
    }
    // Cập nhật các trường
    for (DealerDiscountPolicy policy : policies) {
      // Cập nhật
      policy.setStatus(DiscountPolicyStatus.INACTIVE);
    }
    // Save tất cả cùng lúc
    List<DealerDiscountPolicy> updatedPolicies = dealerDiscountPolicyRepository.saveAll(policies);

    // Map sang response
    List<DealerDiscountPolicyResponse> responses =
        updatedPolicies.stream()
            .map(dealerDiscountPolicyMapper::toResponse)
            .collect(Collectors.toList());
    return APIResponse.success(responses, "Bulk dealer discount policies delete successfully");
  }

  @Override
  public APIResponse<DealerDiscountPolicyResponse> update(
      UUID id, DealerDiscountPolicyRequest request) {
    // Lấy entity theo ID
    DealerDiscountPolicy policy =
        dealerDiscountPolicyRepository
            .findById(id)
            .orElseThrow(
                () -> new GlobalException(ErrorCode.NOT_FOUND, "DealerDiscountPolicy not found"));

    // Cập nhật các trường nếu không null
    if (request.getCustomMultiplier() != null) {
      policy.setCustomMultiplier(request.getCustomMultiplier());
    }
    if (request.getFinalPrice() != null) {
      policy.setFinalPrice(request.getFinalPrice());
    }
    if (request.getEffectiveDate() != null) {
      policy.setExpiryDate(request.getEffectiveDate());
    }
    if (request.getExpiryDate() != null) {
      policy.setExpiryDate(request.getExpiryDate());
    }

    // Cập nhật thời gian
    policy.setUpdateAt(LocalDateTime.now());

    // Lưu vào DB
    DealerDiscountPolicy updatedPolicy = dealerDiscountPolicyRepository.save(policy);

    // Map sang response
    DealerDiscountPolicyResponse response = dealerDiscountPolicyMapper.toResponse(updatedPolicy);

    return APIResponse.success(response);
  }

  @Override
  public APIResponse<DealerDiscountPolicyResponse> delete(UUID id) {
    // Lấy entity theo ID
    DealerDiscountPolicy policy =
        dealerDiscountPolicyRepository
            .findById(id)
            .orElseThrow(
                () -> new GlobalException(ErrorCode.NOT_FOUND, "DealerDiscountPolicy not found"));
    // Cập nhật trạng thái
    policy.setStatus(DiscountPolicyStatus.INACTIVE);
    // Lưu vào DB
    DealerDiscountPolicy updatedPolicy = dealerDiscountPolicyRepository.save(policy);
    // Map sang response
    DealerDiscountPolicyResponse response = dealerDiscountPolicyMapper.toResponse(updatedPolicy);

    return APIResponse.success(response);
  }

  @Override
  public APIResponse<DealerDiscountPolicyResponse> get(UUID id) {
    DealerDiscountPolicy policy =
        dealerDiscountPolicyRepository
            .findById(id)
            .orElseThrow(
                () -> new GlobalException(ErrorCode.NOT_FOUND, "DealerDiscountPolicy not found"));

    DealerDiscountPolicyResponse response = dealerDiscountPolicyMapper.toResponse(policy);
    return APIResponse.success(response);
  }

  @Override
  public APIResponse<PageResponse<DealerDiscountPolicyResponse>> getAll(Pageable pageable) {
    try {
      // Lấy page từ repository
      Page<DealerDiscountPolicy> page = dealerDiscountPolicyRepository.findAll(pageable);

      // Map page sang PageResponse sử dụng pageMapper và dealerDiscountPolicyMapper
      PageResponse<DealerDiscountPolicyResponse> response =
          pageMapper.toPageResponse(page, dealerDiscountPolicyMapper::toResponse);

      return APIResponse.success(response);
    } catch (Exception e) {
      throw new GlobalException(ErrorCode.INVALID_CODE);
    }
  }
}
