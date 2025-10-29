/* EMOB-2025 */
package com.example.emob.service.impl;

import com.example.emob.constant.InstallmentStatus;
import com.example.emob.model.request.installment.InstallmentRequest;
import com.example.emob.model.response.APIResponse;
import com.example.emob.model.response.InstallmentResponse;
import com.example.emob.model.response.PageResponse;
import java.util.List;
import java.util.UUID;
import org.springframework.data.domain.Pageable;

public interface IInstallmentPlan {
  APIResponse<InstallmentResponse> createInstallment(InstallmentRequest request);

  APIResponse<InstallmentResponse> updateInstallmentByStatus(UUID id, InstallmentStatus status);

  APIResponse<InstallmentResponse> viewInstallmentPlan(UUID id);

  APIResponse<PageResponse<InstallmentResponse>> getAllPlansOfDealers(
          List<InstallmentStatus> statuses, Pageable pageable, String keyword);

  // 2️⃣ Đại lý xem plan của chính đại lý mình
  APIResponse<PageResponse<InstallmentResponse>> getAllPlansOfCurrentDealer(
          List<InstallmentStatus> statuses, Pageable pageable, String keyword);

  // 3️⃣ Đại lý xem các plan đã báo giá cho khách hàng cụ thể
  APIResponse<PageResponse<InstallmentResponse>> getAllPlansOfCurrentCustomer(
     UUID customerId, List<InstallmentStatus> statuses, Pageable pageable, String keyword);

  // 4️⃣ Đại lý xem tất cả plan đã báo giá (mọi khách hàng)
  APIResponse<PageResponse<InstallmentResponse>> getAllPlansByCustomer(
      List<InstallmentStatus> statuses, Pageable pageable, String keyword);
}
