package com.example.emob.service;

import com.example.emob.constant.*;
import com.example.emob.entity.SaleContract;
import com.example.emob.entity.SaleOrder;
import com.example.emob.exception.GlobalException;
import com.example.emob.mapper.ContractMapper;
import com.example.emob.mapper.PageMapper;
import com.example.emob.model.response.APIResponse;
import com.example.emob.model.response.ContractResponse;
import com.example.emob.model.response.PageResponse;
import com.example.emob.repository.SaleContractRepository;
import com.example.emob.repository.SaleOrderRepository;
import com.example.emob.service.iml.IContract;
import com.example.emob.util.NotificationHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class ContractService implements IContract {
    @Autowired
    private PageMapper pageMapper;

    @Autowired
    private SaleOrderRepository saleOrderRepository;

    @Autowired
    private ContractMapper contractMapper;

    @Autowired
    private SaleContractRepository contractRepository;

    @Autowired
    private EmailService emailService;

    private String generateContractNumber(UUID orderId) {
        String prefix = "HD-" + LocalDate.now().getYear() + "-";
        String suffix = orderId.toString().substring(0, 6).toUpperCase();
        return prefix + suffix;
    }

    private void remindContract (String contractNumber, String toEmail, String scheduleAt, String cusName) {
        String content = String.format(
                """
                  <p style="color: #4a5568; font-size: 15px; line-height: 1.6; text-align: center; margin: 20px 0;">
                                  Đây là lời nhắc thân thiện từ
                                  <strong>Showroom Ô Tô EMOB</strong>. Quý khách vui lòng đến ký
                                  hợp đồng
                                  <strong>%s</strong>
                                  trước ngày
                                  <strong>%s</strong>.
                              </p>
                """, contractNumber, scheduleAt
        ) ;
        emailService.sendEmail("Nhắc nhở ký hợp đồng",
                "Nhắc nhở ký hợp đồng",
                "Quý khách vui lòng đến ký hợp đồng đúng hẹn",
                NotificationHelper.REMIND_CONTRACT,
                "Chúng tôi rất vui được phục vụ bạn",
                "",
                content,
                "Vui lòng mang theo giấy tờ tùy thân và đến đúng giờ hẹn.",
                cusName,
                "Xem chi tiết hợp đồng",
                toEmail);
    }

    private void signContract (String toEmail, String cusName) {
        emailService.sendEmail("Ký Hợp Đồng Thành Công",
                "Ký Hợp Đồng Thành Công",
                "Cảm ơn bạn đã tin tưởng và lựa chọn dịch vụ của chúng tôi",
                NotificationHelper.SIGN_CONTRACT,
                "Chúng tôi xin thông báo rằng hợp đồng của bạn đã được ký kết thành công.",
                "",
                "Hợp đồng của bạn đã được xác nhận và lưu trữ an toàn trong hệ thống. Bộ phận chăm sóc khách hàng sẽ sớm liên hệ để hướng dẫn các bước tiếp theo.",
                "Vui lòng kiểm tra email của bạn để xem chi tiết hợp đồng và thông tin thanh toán.",
                cusName,
                "Xem chi tiết hợp đồng",
                toEmail);
    }

    @Override
//    @PreAuthorize("hasRole('EVM_STAFF') or hasRole('DEALER_STAFF')")
    public APIResponse<ContractResponse> createContract(UUID orderId) {
        String contractNumber = generateContractNumber(orderId);
        SaleOrder order = saleOrderRepository.findById(orderId)
                .filter((item) -> (item.getOrderStatus().equals(OrderStatus.CREATED)))
//        (item.getPaymentStatus().equals(PaymentStatus.PAID)
                .orElseThrow(() -> new GlobalException(ErrorCode.NOT_FOUND));
        SaleContract contract = contractMapper.toSaleContract(order);
        contract.setSaleOrder(order);
        contract.setContractNumber(contractNumber);
        contract.setCreateAt(LocalDateTime.now());
        contract.setStatus(ContractStatus.PENDING);
        contractRepository.save(contract);
//        if (order.getAccount().getRole().equals(Role.EVM_STAFF)) {
//            remindContract(contract.getContractNumber(), order.getDealer(),
//                    order.getScheduleAt(), order.getAccount().getFullName());
//        } else {
            remindContract(contract.getContractNumber(), "zuongm52@gmail.com",
                    order.getScheduleAt().toLocalDate().toString(), "Vuong");
//        }
        ContractResponse contractResponse = contractMapper.toContractResponse(contract);
        return APIResponse.success(contractResponse, "Create contract successfully");
    }


    @Override
    public APIResponse<ContractResponse> viewContract(UUID id) {
        SaleContract contract = contractRepository.findById(id)
                .orElseThrow(() -> new GlobalException(ErrorCode.NOT_FOUND));
        ContractResponse contractResponse = contractMapper.toContractResponse(contract);
        return APIResponse.success(contractResponse, "View Contract Successfully");
    }

    @Override
    public APIResponse<PageResponse<ContractResponse>> viewAllContracts(Pageable pageable) {
        Page<SaleContract> contracts = contractRepository.findAll(pageable);
        PageResponse<ContractResponse> contractResponsePageResponse =
                        pageMapper.toPageResponse(contracts, contractMapper::toContractResponse);
        return APIResponse.success(contractResponsePageResponse, "View all contracts successfully");
    }

    @Override
    public APIResponse<Void> cancelContract(UUID id) {
        SaleContract contract = contractRepository.findById(id)
                .filter((item) -> item.getStatus().equals(ContractStatus.PENDING))
                .orElseThrow(() -> new GlobalException(ErrorCode.NOT_FOUND));
        contract.setStatus(ContractStatus.TERMINATED);
        contractRepository.save(contract);
        return APIResponse.error(200, "Cancel contract successfully");
    }

    @Override
    public APIResponse<ContractResponse> signContract(UUID contractId) {
        // sign contract chưa check role
        SaleContract contract = contractRepository.findById(contractId)
                .filter((item) -> item.getStatus().equals(ContractStatus.PENDING))
                .orElseThrow(() -> new GlobalException(ErrorCode.NOT_FOUND));
        if (LocalDateTime.now().isAfter(contract.getSaleOrder().getScheduleAt())) {
            throw new GlobalException(ErrorCode.EXPIRED_CONTRACT);
        }
        contract.setStatus(ContractStatus.SIGNED);
        contract.setSignDate(LocalDateTime.now());
        contractRepository.save(contract);
        signContract(contract.getSaleOrder().getCustomer().getEmail(), contract.getSaleOrder().getCustomer().getFullName());
        ContractResponse contractResponse = contractMapper.toContractResponse(contract);
        return APIResponse.success(contractResponse, "Signed contract successfully");
    }

    @Override
    public APIResponse<ContractResponse> updateContractStatus(UUID contractId) {
        SaleContract contract = contractRepository.findById(contractId)
                .filter((item) -> item.getStatus().equals(ContractStatus.SIGNED))
                .orElseThrow(() -> new GlobalException(ErrorCode.NOT_FOUND));
        contract.setStatus(ContractStatus.COMPLETED);
        contractRepository.save(contract);
        ContractResponse contractResponse = contractMapper.toContractResponse(contract);
        return APIResponse.success(contractResponse, "Update contract status successfully");
    }
}
