package com.example.emob.service.impl;

import com.example.emob.model.request.dealerDiscountPolicy.DealerDiscountPolicyBulkDeleteRequest;
import com.example.emob.model.request.dealerDiscountPolicy.DealerDiscountPolicyBulkRequest;
import com.example.emob.model.request.dealerDiscountPolicy.DealerDiscountPolicyRequest;
import com.example.emob.model.response.APIResponse;
import com.example.emob.model.response.DealerDiscountPolicyResponse;
import com.example.emob.model.response.PageResponse;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.UUID;

public interface IDealerDiscountPolicy {

    APIResponse<List<DealerDiscountPolicyResponse>> bulkCreate(DealerDiscountPolicyBulkRequest request);
    APIResponse<List<DealerDiscountPolicyResponse>> bulkUpdate(DealerDiscountPolicyBulkRequest request);
    APIResponse<List<DealerDiscountPolicyResponse>> bulkDelete(DealerDiscountPolicyBulkDeleteRequest request);
    APIResponse<DealerDiscountPolicyResponse> update(UUID id, DealerDiscountPolicyRequest request);
    APIResponse<DealerDiscountPolicyResponse> delete(UUID id);

    APIResponse<DealerDiscountPolicyResponse> get(UUID id);

    APIResponse<PageResponse<DealerDiscountPolicyResponse>> getAll(Pageable pageable);
}
