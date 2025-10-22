/* EMOB-2025 */
package com.example.emob.service.impl;

import com.example.emob.model.request.quotation.QuotationItemRequest;
import com.example.emob.model.request.quotation.QuotationItemUpdateRequest;
import com.example.emob.model.request.quotation.QuotationRequest;
import com.example.emob.model.response.APIResponse;
import com.example.emob.model.response.PageResponse;
import com.example.emob.model.response.quotation.QuotationResponse;
import java.util.UUID;
import org.springframework.data.domain.Pageable;

public interface IQuotation {
  APIResponse<QuotationResponse> create(QuotationRequest<QuotationItemRequest> request);

  APIResponse<QuotationResponse> update(
      UUID id, QuotationRequest<QuotationItemUpdateRequest> request);

  APIResponse<QuotationResponse> delete(UUID id);

  APIResponse<QuotationResponse> get(UUID id);

  APIResponse<PageResponse<QuotationResponse>> getAll(Pageable pageable);
}
