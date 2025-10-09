
package com.example.emob.service.impl;


import com.example.emob.model.request.quotation.QuotationRequest;
import com.example.emob.model.response.APIResponse;
import com.example.emob.model.response.PageResponse;
import com.example.emob.model.response.QuotationResponse;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface IQuotation {
    APIResponse<QuotationResponse> create(QuotationRequest request);

    APIResponse<QuotationResponse> update(UUID id, QuotationRequest request);

    APIResponse<QuotationResponse> delete(UUID id);

    APIResponse<QuotationResponse> get(UUID id);

    APIResponse<PageResponse<QuotationResponse>> getAll(Pageable pageable);

}

