/* EMOB-2025 */
package com.example.emob.service.impl;

import com.example.emob.constant.ContractStatus;
import com.example.emob.constant.PaymentStatus;
import com.example.emob.entity.SaleOrder;
import com.example.emob.model.request.installment.InstallmentRequest;
import com.example.emob.model.response.APIResponse;
import com.example.emob.model.response.PageResponse;
import com.example.emob.model.response.saleContract.ContractResponse;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import org.springframework.data.domain.Pageable;

public interface IContract {

  APIResponse<ContractResponse> createContract(SaleOrder saleOrder);

  APIResponse<PageResponse<ContractResponse>> getAllContractsOfDealers(
      String keyword, List<ContractStatus> statuses, Pageable pageable);

  APIResponse<PageResponse<ContractResponse>> getAllContractsByCustomer(
      String keyword, List<ContractStatus> statuses, Pageable pageable);

  APIResponse<PageResponse<ContractResponse>> getAllContractsOfCurrentDealer(
      String keyword, List<ContractStatus> statuses, Pageable pageable);

  APIResponse<PageResponse<ContractResponse>> getAllContractsOfCurrentCustomer(
      UUID customerId, String keyword, List<ContractStatus> statuses, Pageable pageable);

  APIResponse<ContractResponse> getContractById(UUID contractId);

  public APIResponse<ContractResponse> signContract(
      LocalDate date, PaymentStatus status, InstallmentRequest request);

  APIResponse<Void> cancelContract(UUID contractId);
}
