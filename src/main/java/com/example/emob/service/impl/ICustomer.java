/* EMOB-2025 */
package com.example.emob.service.impl;

import com.example.emob.constant.CustomerStatus;
import com.example.emob.model.request.CustomerRequest;
import com.example.emob.model.response.APIResponse;
import com.example.emob.model.response.CustomerResponse;
import com.example.emob.model.response.PageResponse;
import java.util.UUID;
import org.springframework.data.domain.Pageable;

public interface ICustomer {
  APIResponse<CustomerResponse> create(CustomerRequest request);

  APIResponse<CustomerResponse> update(UUID id, CustomerRequest request);

  APIResponse<CustomerResponse> delete(UUID id);

  APIResponse<CustomerResponse> get(UUID id);

  APIResponse<PageResponse<CustomerResponse>> getAll(Pageable pageable,
                                                     String keyword,
                                                     CustomerStatus status);
}
