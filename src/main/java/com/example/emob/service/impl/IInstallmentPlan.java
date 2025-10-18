/* EMOB-2025 */
package com.example.emob.service.impl;

import com.example.emob.constant.InstallmentStatus;
import com.example.emob.model.request.installment.InstallmentRequest;
import com.example.emob.model.response.APIResponse;
import com.example.emob.model.response.InstallmentResponse;
import com.example.emob.model.response.PageResponse;
import java.util.UUID;
import org.springframework.data.domain.Pageable;

public interface IInstallmentPlan {
  APIResponse<InstallmentResponse> createInstallment(InstallmentRequest request);

  APIResponse<InstallmentResponse> updateInstallmentByStatus(UUID id, InstallmentStatus status);

  APIResponse<InstallmentResponse> viewInstallmentPlan(UUID id);

  APIResponse<PageResponse<InstallmentResponse>> viewAllInstallmentPlans(Pageable pageable);
}
