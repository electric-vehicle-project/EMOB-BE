package com.example.emob.service.impl;

import com.example.emob.model.request.contract.CreateContractRequest;
import com.example.emob.model.response.APIResponse;
import com.example.emob.model.response.ContractResponse;
import com.example.emob.model.response.PageResponse;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface IContract {
    APIResponse<ContractResponse> createContract (UUID orderId);
    APIResponse<ContractResponse> viewContract (UUID contractId);
    APIResponse<PageResponse<ContractResponse>> viewAllContracts (Pageable pageable);
    APIResponse<Void> cancelContract (UUID contractId);

    APIResponse<ContractResponse> signContract (UUID contractId);
    APIResponse<ContractResponse> updateContractStatus (UUID contractId);
}
