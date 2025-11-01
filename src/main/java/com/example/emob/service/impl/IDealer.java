/* EMOB-2025 */
package com.example.emob.service.impl;

import com.example.emob.constant.ContractStatus;
import com.example.emob.model.request.DealerRequest;
import com.example.emob.model.response.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import org.springframework.data.domain.Pageable;

public interface IDealer {
  APIResponse<DealerResponse> create(DealerRequest request);

  APIResponse<DealerResponse> update(UUID id, DealerRequest request);

  APIResponse<DealerResponse> delete(UUID id);

  APIResponse<DealerResponse> get(UUID id);

  APIResponse<PageResponse<DealerResponse>> getAll(
      Pageable pageable, String keyword, String country);

  APIResponse<DealerRevenueResponse>  getDealerRevenueReport(List<ContractStatus> statuses, Pageable pageable);
}
