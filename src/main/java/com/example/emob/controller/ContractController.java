/* EMOB-2025 */
package com.example.emob.controller;

import com.example.emob.constant.ContractStatus;
import com.example.emob.model.response.APIResponse;
import com.example.emob.model.response.PageResponse;
import com.example.emob.model.response.saleContract.ContractResponse;
import com.example.emob.service.ContractService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/contract")
@CrossOrigin("*")
@Tag(name = "Contract Controller", description = "Endpoints for contract management")
@SecurityRequirement(name = "api")
public class ContractController {
  @Autowired ContractService contractService;

  private Pageable buildPageable(int page, int size, String[] sort) {
    Sort sortObj = Sort.by(Arrays.stream(sort)
            .map(s -> {
              String[] _s = s.split(",");
              return new Sort.Order(
                      _s.length > 1 && _s[1].equalsIgnoreCase("desc") ? Sort.Direction.DESC : Sort.Direction.ASC,
                      _s[0]
              );
            })
            .toList());
    return PageRequest.of(page, size, sortObj);
  }

  @GetMapping("/dealers")
  @Operation(summary = "Hãng xe xem tất cả hợp đồng của đại lý")
  public APIResponse<PageResponse<ContractResponse>> getAllContractsOfDealers(
          @RequestParam(required = false) String keyword,
          @RequestParam(required = false) List<ContractStatus> statuses,
          @RequestParam(defaultValue = "0") int page,
          @RequestParam(defaultValue = "10") int size,
          @RequestParam(defaultValue = "id,desc") String[] sort) {

    Pageable pageable = buildPageable(page, size, sort);
    return contractService.getAllContractsOfDealers(keyword, statuses, pageable);
  }

  @GetMapping("/customers/{customerId}")
  @Operation(summary = "Đại lý xem hợp đồng của khách hàng cụ thể (qua báo giá)")
  public APIResponse<PageResponse<ContractResponse>> getAllContractsOfCurrentCustomer(
          @PathVariable UUID customerId,
          @RequestParam(required = false) String keyword,
          @RequestParam(required = false) List<ContractStatus> statuses,
          @RequestParam(defaultValue = "0") int page,
          @RequestParam(defaultValue = "10") int size,
          @RequestParam(defaultValue = "id,desc") String[] sort) {

    Pageable pageable = buildPageable(page, size, sort);
    return contractService.getAllContractsOfCurrentCustomer(customerId, keyword, statuses, pageable);
  }

  @GetMapping("/current-dealer")
  @Operation(summary = "Đại lý xem hợp đồng của chính mình")
  public APIResponse<PageResponse<ContractResponse>> getAllContractsOfCurrentDealer(
          @RequestParam(required = false) String keyword,
          @RequestParam(required = false) List<ContractStatus> statuses,
          @RequestParam(defaultValue = "0") int page,
          @RequestParam(defaultValue = "10") int size,
          @RequestParam(defaultValue = "id,desc") String[] sort) {

    Pageable pageable = buildPageable(page, size, sort);
    return contractService.getAllContractsOfCurrentDealer(keyword, statuses, pageable);
  }

  @GetMapping("/dealer/customers")
  @Operation(summary = "Đại lý xem hợp đồng của tất cả khách hàng của mình (qua báo giá)")
  public APIResponse<PageResponse<ContractResponse>> getAllContractsByCustomer(
          @RequestParam(required = false) String keyword,
          @RequestParam(required = false) List<ContractStatus> statuses,
          @RequestParam(defaultValue = "0") int page,
          @RequestParam(defaultValue = "10") int size,
          @RequestParam(defaultValue = "id,desc") String[] sort) {

    Pageable pageable = buildPageable(page, size, sort);
    return contractService.getAllContractsByCustomer(keyword, statuses, pageable);
  }

  // ===========================================
  // 🔹 5. Lấy chi tiết 1 hợp đồng
  // ===========================================
  @GetMapping("/{contractId}")
  public ResponseEntity<APIResponse<ContractResponse>> getContractById(
      @PathVariable UUID contractId) {
    APIResponse<ContractResponse> response = contractService.getContractById(contractId);
    return ResponseEntity.ok(response);
  }

  // ===========================================
  // 🔹 7. Ký hợp đồng
  // ===========================================
  @PostMapping("/sign/{contractId}")
  public ResponseEntity<APIResponse<ContractResponse>> signContract(
      @PathVariable UUID contractId, @RequestParam LocalDate date) {
    APIResponse<ContractResponse> response = contractService.signContract(date, contractId);
    return ResponseEntity.ok(response);
  }

  // ===========================================
  // 🔹 8. Hủy hợp đồng
  // ===========================================
  @PostMapping("/cancel/{contractId}")
  public ResponseEntity<APIResponse<Void>> cancelContract(@PathVariable UUID contractId) {
    APIResponse<Void> response = contractService.cancelContract(contractId);
    return ResponseEntity.ok(response);
  }
}
