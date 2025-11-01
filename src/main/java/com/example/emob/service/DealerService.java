/* EMOB-2025 */
package com.example.emob.service;

import com.example.emob.constant.ContractStatus;
import com.example.emob.constant.ErrorCode;
import com.example.emob.entity.Dealer;
import com.example.emob.entity.Inventory;
import com.example.emob.exception.GlobalException;
import com.example.emob.mapper.DealerMapper;
import com.example.emob.mapper.PageMapper;
import com.example.emob.model.request.DealerRequest;
import com.example.emob.model.response.*;
import com.example.emob.repository.DealerRepository;
import com.example.emob.repository.SaleContractRepository;
import com.example.emob.service.impl.IDealer;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.function.Function;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

@Service
public class DealerService implements IDealer {

  @Autowired DealerRepository dealerRepository;

  @Autowired DealerMapper dealerMapper;

  @Autowired
  SaleContractRepository saleContractRepository;

  @Autowired PageMapper pageMapper;

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
  @PreAuthorize("hasAnyRole('EVM_STAFF','ADMIN')")
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
  //  @PreAuthorize("hasAnyRole('EVM_STAFF','ADMIN')")
  public APIResponse<PageResponse<DealerResponse>> getAll(
      Pageable pageable, String keyword, String country) {
    try {
      Page<Dealer> page = dealerRepository.searchAndFilter(keyword, country, pageable);
      PageResponse<DealerResponse> response =
          pageMapper.toPageResponse(page, dealerMapper::toDealerResponse);

      return APIResponse.success(response, "Get all dealers successfully");
    } catch (DataAccessException ex) {
      throw new GlobalException(ErrorCode.DB_ERROR, "Database access error");
    } catch (Exception ex) {
      throw new GlobalException(ErrorCode.OTHER, "Unexpected error occurred");
    }
  }

  @Override
  @PreAuthorize("hasRole('ADMIN')")
  public APIResponse<DealerRevenueResponse> getDealerRevenueReport(
          List<ContractStatus> statuses,
          Pageable pageable) {

    // Lấy toàn bộ dữ liệu
    List<DealerRevenueItemResponse> all =
            dealerRepository.getDealerRevenueByDateRange(statuses);

    long totalDealers = all.size();

    // Phân trang thủ công
    int start = (int) pageable.getOffset();
    int end = Math.min(start + pageable.getPageSize(), all.size());
    List<DealerRevenueItemResponse> paged = start >= all.size() ? List.of() : all.subList(start, end);

    // Tạo response wrapper
    DealerRevenueResponse response = DealerRevenueResponse.builder()
            .items(paged)
            .totalDealer(totalDealers)
            .build();

    return APIResponse.success(response);
  }

}
