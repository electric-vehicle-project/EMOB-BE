package com.example.emob.service.impl;

import com.example.emob.constant.InstallmentStatus;
import com.example.emob.model.request.installment.InstallmentRequest;
import com.example.emob.model.response.APIResponse;
import com.example.emob.model.response.InstallmentResponse;
import com.example.emob.model.response.PageResponse;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface IInstallmentPlan {
    APIResponse<InstallmentResponse> createInstallment (InstallmentRequest request);

    APIResponse<InstallmentResponse> updateInstallmentByStatus (UUID id, InstallmentStatus status);

    APIResponse<InstallmentResponse> viewInstallmentPlan (UUID id);

    APIResponse<PageResponse<InstallmentResponse>> viewAllInstallmentPlans (Pageable pageable);
}
