package com.example.emob.service.iml;

import com.example.emob.model.request.DealerRequest;
import com.example.emob.model.request.LoginRequest;
import com.example.emob.model.response.APIResponse;
import com.example.emob.model.response.AccountResponse;
import com.example.emob.model.response.DealerResponse;
import com.example.emob.model.response.PageResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface IDealer {
    APIResponse<DealerResponse> create (DealerRequest request);
    APIResponse<DealerResponse> update (UUID id, DealerRequest request);
    APIResponse<DealerResponse> delete (UUID id);
    APIResponse<DealerResponse> get (UUID id);
    APIResponse<PageResponse<DealerResponse>> getAll (Pageable pageable);
}
