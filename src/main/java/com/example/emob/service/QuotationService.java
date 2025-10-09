
package com.example.emob.service;

import com.example.emob.mapper.QuotationMapper;
import com.example.emob.model.request.quotation.QuotationRequest;
import com.example.emob.model.response.APIResponse;
import com.example.emob.model.response.PageResponse;
import com.example.emob.model.response.QuotationResponse;
import com.example.emob.repository.QuotationRepository;
import com.example.emob.service.impl.IQuotation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class QuotationService implements IQuotation {
      @Autowired
      private QuotationRepository quotationRepository;
      @Autowired
      private QuotationMapper quotationMapper;


      @Override
      public APIResponse<QuotationResponse> create(QuotationRequest request) {
            return null;
      }

      @Override
      public APIResponse<QuotationResponse> update(UUID id, QuotationRequest request) {
            return null;
      }

      @Override
      public APIResponse<QuotationResponse> delete(UUID id) {
            return null;
      }

      @Override
      public APIResponse<QuotationResponse> get(UUID id) {
            return null;
      }

      @Override
      public APIResponse<PageResponse<QuotationResponse>> getAll(Pageable pageable) {
            return null;
      }
}
