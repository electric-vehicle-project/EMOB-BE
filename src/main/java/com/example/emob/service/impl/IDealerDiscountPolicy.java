/* EMOB-2025 */
package com.example.emob.service.impl;

import com.example.emob.constant.DiscountPolicyStatus;
import com.example.emob.model.request.dealerDiscountPolicy.DealerDiscountPolicyBulkDeleteRequest;
import com.example.emob.model.request.dealerDiscountPolicy.DealerDiscountPolicyBulkRequest;
import com.example.emob.model.request.dealerDiscountPolicy.DealerDiscountPolicyRequest;
import com.example.emob.model.response.APIResponse;
import com.example.emob.model.response.DealerDiscountPolicyResponse;
import com.example.emob.model.response.PageResponse;
import java.util.List;
import java.util.UUID;
import org.springframework.data.domain.Pageable;

public interface IDealerDiscountPolicy {

  APIResponse<List<DealerDiscountPolicyResponse>> bulkCreate(
      DealerDiscountPolicyBulkRequest request);

  APIResponse<List<DealerDiscountPolicyResponse>> bulkUpdate(
      DealerDiscountPolicyBulkRequest request);

  APIResponse<List<DealerDiscountPolicyResponse>> bulkDelete(
      DealerDiscountPolicyBulkDeleteRequest request);

  APIResponse<DealerDiscountPolicyResponse> update(UUID id, DealerDiscountPolicyRequest request);

  APIResponse<DealerDiscountPolicyResponse> delete(UUID id);

  APIResponse<DealerDiscountPolicyResponse> get(UUID id);

  APIResponse<PageResponse<DealerDiscountPolicyResponse>> getAll(
      Pageable pageable, String keyword, List<DiscountPolicyStatus> status);
}
