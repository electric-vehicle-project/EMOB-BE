/* EMOB-2025 */
package com.example.emob.service;

import com.example.emob.constant.*;
import com.example.emob.entity.*;
import com.example.emob.exception.GlobalException;
import com.example.emob.mapper.ContractMapper;
import com.example.emob.mapper.PageMapper;
import com.example.emob.model.response.APIResponse;
import com.example.emob.model.response.PageResponse;
import com.example.emob.model.response.saleContract.ContractResponse;
import com.example.emob.repository.CustomerRepository;
import com.example.emob.repository.SaleContractRepository;
import com.example.emob.repository.SaleOrderRepository;
import com.example.emob.service.impl.IContract;
import com.example.emob.util.AccountUtil;
import com.example.emob.util.NotificationHelper;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ContractService implements IContract {
  @Autowired private PageMapper pageMapper;

  @Autowired private SaleOrderRepository saleOrderRepository;

  @Autowired private ContractMapper contractMapper;

  @Autowired private SaleContractRepository contractRepository;

  @Autowired private EmailService emailService;
  @Autowired private CustomerRepository customerRepository;

  private String generateContractNumber(UUID orderId) {
    String prefix = "HD-" + LocalDate.now().getYear() + "-";
    String suffix = orderId.toString().substring(0, 6).toUpperCase();
    return prefix + suffix;
  }

  private void signContract(String toEmail, String cusName) {
    emailService.sendEmail(
        "Ký Hợp Đồng Thành Công",
        "Ký Hợp Đồng Thành Công",
        "Cảm ơn bạn đã tin tưởng và lựa chọn dịch vụ của chúng tôi",
        NotificationHelper.SIGN_CONTRACT,
        "Chúng tôi xin thông báo rằng hợp đồng của bạn đã được ký kết thành công.",
        "",
        "Hợp đồng của bạn đã được xác nhận và lưu trữ an toàn trong hệ thống. Bộ phận chăm sóc"
            + " khách hàng sẽ sớm liên hệ để hướng dẫn các bước tiếp theo.",
        "Vui lòng kiểm tra email của bạn để xem chi tiết hợp đồng và thông tin thanh toán.",
        cusName,
        "Xem chi tiết hợp đồng",
        toEmail);
  }

  @Override
  @Transactional
  public APIResponse<ContractResponse> createContract(SaleOrder saleOrder) {
    // 🔹 1. Kiểm tra hợp lệ
    if (saleOrder == null) {
      throw new GlobalException(ErrorCode.INVALID_CODE, "Sale order not found");
    }
    if (saleOrder.getContract() != null) {
      throw new GlobalException(ErrorCode.INVALID_CODE, "This order already has a contract");
    }

    // 🔹 2. Tạo đối tượng hợp đồng
    SaleContract contract = new SaleContract();
    contract.setContractNumber(generateContractNumber(saleOrder.getId()));
    contract.setCreateAt(LocalDateTime.now());
    contract.setStatus(ContractStatus.PENDING);
    contract.setSaleOrder(saleOrder);
    contract.setTotalPrice(saleOrder.getTotalPrice());
    contract.setTotalQuantity(saleOrder.getTotalQuantity());
    if (saleOrder.getVatAmount() != null) {
      contract.setVatAmount(saleOrder.getVatAmount());
    }
    contract.setAccount(AccountUtil.getCurrentUser());

    // 🔹 3. Map từ SaleOrderItem → SaleContractItem (dùng mapper)
    Set<SaleContractItem> contractItems = saleOrder.getSaleOrderItems().stream().filter(vri -> !vri.isDeleted()) // bỏ qua item bị xóa
                 .map(vri -> {
                   SaleContractItem item = new SaleContractItem();
                   item.setUnitPrice(vri.getUnitPrice());
                   item.setTotalPrice(vri.getTotalPrice());
                   item.setDiscountPrice(BigDecimal.ZERO);
                   item.setQuantity(vri.getQuantity());
                   item.setColor(vri.getColor());
                   item.setVehicleStatus(vri.getVehicleStatus());
                   item.setVehicle(vri.getVehicle()); // đảm bảo vehicle đã managed
                   item.setSaleContract(contract);
                   // Tạo vehicleUnits riêng cho item, không share với item khác
                   item.getVehicleUnits().forEach((unit) -> {
                     unit.setSaleContractItem(item);
                   });
                   return item;
                 })
                 .collect(Collectors.toSet());
    System.out.println("Items count: " + contractItems.size());
    contractItems.forEach(i -> System.out.println(i));
    contract.setSaleContractItems(contractItems);
    saleOrder.setContract(contract);
    // 🔹 4. Lưu hợp đồng
    SaleContract savedContract = contractRepository.save(contract);

    // 🔹 5. Map sang response
    ContractResponse response = contractMapper.toContractResponse(savedContract);

    return APIResponse.success(response, "Contract created successfully");
  }

  @Override
  @PreAuthorize("hasAnyRole('EVM_STAFF', 'DEALER_STAFF')")
  public APIResponse<Void> cancelContract(UUID id) {
    SaleContract contract =
        contractRepository
            .findById(id)
            .filter((item) -> item.getStatus().equals(ContractStatus.PENDING))
            .orElseThrow(() -> new GlobalException(ErrorCode.NOT_FOUND));
    contract.setStatus(ContractStatus.TERMINATED);
    contractRepository.save(contract);
    return APIResponse.error(200, "Cancel contract successfully");
  }

  @Override
  @PreAuthorize("hasAnyRole('EVM_STAFF', 'DEALER_STAFF')")
  public APIResponse<ContractResponse> signContract(LocalDate date, UUID contractId) {
    // sign contract chưa check role
    SaleContract contract =
        contractRepository
            .findById(contractId)
            .filter((item) -> item.getStatus().equals(ContractStatus.PENDING))
            .orElseThrow(() -> new GlobalException(ErrorCode.NOT_FOUND));
    contract.setStatus(ContractStatus.SIGNED);

    if (contract.getSaleOrder().getQuotation() != null) {
      String email = contract.getSaleOrder().getQuotation().getCustomer().getEmail();
      String cusName = contract.getSaleOrder().getQuotation().getCustomer().getFullName();
      contract.getSaleContractItems()
              .forEach(item -> item.getVehicleUnits()
                      .forEach(unit -> unit.setPurchaseDate(LocalDateTime.now())));
      signContract(email, cusName);
    }
    contract.setSignDate(date);
    contractRepository.save(contract);
    ContractResponse contractResponse = contractMapper.toContractResponse(contract);
    return APIResponse.success(contractResponse, "Signed contract successfully");
  }

  // ===========================================
  // 🔹 1. Hãng xe xem tất cả hợp đồng của đại lý
  // ===========================================
  @Override
  @PreAuthorize("hasAnyRole('EVM_STAFF', 'ADMIN')")
  public APIResponse<PageResponse<ContractResponse>> getAllContractsOfDealers(
      String keyword, List<ContractStatus> statuses, Pageable pageable) {
    Page<SaleContract> page =
        contractRepository.findAllWithVehicleRequest(statuses, keyword, pageable);
    PageResponse<ContractResponse> response =
        pageMapper.toPageResponse(page, contractMapper::toContractResponse);
    return APIResponse.success(response);
  }

  // ===========================================
  // 🔹 2. Khách hàng xem hợp đồng của chính mình
  // ===========================================
  @Override
  @PreAuthorize("hasAnyRole('DEALER_STAFF', 'MANAGER')")
  public APIResponse<PageResponse<ContractResponse>> getAllContractsOfCurrentCustomer(
      UUID customerId, String keyword, List<ContractStatus> statuses, Pageable pageable) {
    Dealer dealer = AccountUtil.getCurrentUser().getDealer();
    Customer customer =
        customerRepository
            .findById(customerId)
            .orElseThrow(() -> new GlobalException(ErrorCode.NOT_FOUND, "Customer not found"));

    Page<SaleContract> page =
        contractRepository.findAllWithQuotationByDealerAndStatuses(
            dealer, customer, statuses, keyword, pageable);

    PageResponse<ContractResponse> response =
        pageMapper.toPageResponse(page, contractMapper::toContractResponse);
    return APIResponse.success(response);
  }

  // ===========================================
  // 🔹 3. Đại lý xem hợp đồng của mình
  // ===========================================
  @Override
  @PreAuthorize("hasAnyRole('DEALER_STAFF', 'MANAGER')")
  public APIResponse<PageResponse<ContractResponse>> getAllContractsOfCurrentDealer(
      String keyword, List<ContractStatus> statuses, Pageable pageable) {
    Dealer dealer = AccountUtil.getCurrentUser().getDealer();
    Page<SaleContract> page =
        contractRepository.findAllWithVehicleRequestByDealerAndStatuses(
            dealer, statuses, keyword, pageable);
    PageResponse<ContractResponse> response =
        pageMapper.toPageResponse(page, contractMapper::toContractResponse);
    return APIResponse.success(response);
  }

  // ===========================================
  // 🔹 4. Đại lý xem hợp đồng của khách hàng của mình
  // ===========================================
  @Override
  @PreAuthorize("hasAnyRole('DEALER_STAFF', 'MANAGER')")
  public APIResponse<PageResponse<ContractResponse>> getAllContractsByCustomer(
      String keyword, List<ContractStatus> statuses, Pageable pageable) {
    Dealer dealer = AccountUtil.getCurrentUser().getDealer();
    Page<SaleContract> page =
        contractRepository.findAllWithQuotationByDealerAndCustomerAndStatuses(
            dealer, statuses, keyword, pageable);
    PageResponse<ContractResponse> response =
        pageMapper.toPageResponse(page, contractMapper::toContractResponse);
    return APIResponse.success(response);
  }

  // ===========================================
  // 🔹 5. Lấy chi tiết 1 hợp đồng
  // ===========================================
  @Override
  public APIResponse<ContractResponse> getContractById(UUID contractId) {
    SaleContract contract =
        contractRepository
            .findById(contractId)
            .orElseThrow(() -> new GlobalException(ErrorCode.NOT_FOUND, "Contract not found"));
    return APIResponse.success(contractMapper.toContractResponse(contract));
  }
}
