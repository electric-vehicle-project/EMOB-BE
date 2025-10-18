package com.example.emob.service;

import com.example.emob.constant.ErrorCode;
import com.example.emob.constant.InstallmentStatus;
import com.example.emob.constant.PaymentStatus;
import com.example.emob.entity.InstallmentPlan;
import com.example.emob.entity.SaleOrder;
import com.example.emob.exception.GlobalException;
import com.example.emob.mapper.InstallmentPlanMapper;
import com.example.emob.mapper.PageMapper;
import com.example.emob.model.request.installment.InstallmentRequest;
import com.example.emob.model.request.installment.UpdateInstallmentRequest;
import com.example.emob.model.response.APIResponse;
import com.example.emob.model.response.InstallmentResponse;
import com.example.emob.model.response.PageResponse;
import com.example.emob.repository.InstallmentPlanRepository;
import com.example.emob.repository.SaleOrderRepository;
import com.example.emob.service.impl.IInstallmentPlan;
import com.example.emob.util.NotificationHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class InstallmentPlanService implements IInstallmentPlan {
    @Autowired
    InstallmentPlanMapper installmentPlanMapper;

    @Autowired
    SaleOrderRepository saleOrderRepository;

    @Autowired
    InstallmentPlanRepository installmentPlanRepository;

    @Autowired
    PageMapper pageMapper;

    @Autowired
    EmailService emailService;

//    @Scheduled(cron = "0 0 8 * * *") // mỗi ngày 8h sẽ chạy tự dộng
    @Scheduled(fixedRate = 60000)
    public void remindOverdueDaily() {
        LocalDate today = LocalDate.now();
        // quá hạn nhưng chưa nhắc hôm nay
        List<InstallmentPlan> plans = installmentPlanRepository.findAllOverdueNeedingReminder(today);

        for (InstallmentPlan p : plans) {
            // Cập nhật trạng thái nếu chưa set OVERDUE
            if (p.getNextDueDate().isBefore(today) && p.getStatus() != InstallmentStatus.PAID) {
                p.setStatus(InstallmentStatus.OVERDUE);
            }

            // Gửi email nhắc quá hạn
            String content = remindInstallmentOverdue(p.getSaleOrder().getCustomer().getFullName(),
                    p.getMonthlyAmount(), p.getNextDueDate());
            emailService.sendEmail(
                    "Thông báo quá hạn thanh toán đơn hàng ",
                    "Quá hạn thanh toán trả góp",
                    "Thanh toán hợp đồng trả góp bị trễ hạn",
                    NotificationHelper.INSTALLMENT_OVERDUE,
                    "Vui lòng thanh toán ngay để tránh bị tính phí trễ hạn.",
                    "",
                    content,
                    "Nếu đã thanh toán, vui lòng bỏ qua email này.",
                    p.getSaleOrder().getCustomer().getFullName(),
                    "Thanh toán ngay",
                    p.getSaleOrder().getCustomer().getEmail()
            );

            // cấm gửi trùng trong ngày
            p.setLastReminderDate(today);
            p.setReminderCount((p.getReminderCount() == null ? 0 : p.getReminderCount()) + 1 );
            p.setUpdateAt(LocalDateTime.now());
            installmentPlanRepository.save(p);
        }
    }


    private String sendInstallmentCreatedEmail(
            String cusName,
            BigDecimal totalAmount,
            BigDecimal deposit,
            BigDecimal monthlyAmount,
            int termMonths,
            LocalDate nextDueDate
    ) {
        return String.format("""
        <p style="font-size:15px; color:#2d3748; line-height:1.6; text-align:center;">
            Kính gửi <strong>%s</strong>,<br>
            Kế hoạch trả góp cho đơn hàng 
            đã được khởi tạo thành công.
        </p>

        <div style="margin: 20px auto; width: 80%%; border: 1px solid #e2e8f0; border-radius: 10px; padding: 15px;">
            <p style="font-size:14px; color:#4a5568;">📄 <strong>Chi tiết kế hoạch trả góp:</strong></p>
            <ul style="list-style-type:none; padding-left:0; font-size:14px; color:#4a5568;">
                <li>Tổng giá trị: <strong style="color:#2b6cb0;">%,.0f VND</strong></li>
                <li>Tiền đặt cọc: <strong style="color:#2b6cb0;">%,.0f VND</strong></li>
                <li>Thời hạn: <strong>%d tháng</strong></li>
                <li>Số tiền trả mỗi tháng: <strong style="color:#e53e3e;">%,.0f VND</strong></li>
                <li>Kỳ thanh toán đầu tiên: <strong>%s</strong></li>
            </ul>
        </div>

        <p style="font-size:14px; color:#718096; text-align:center;">
            Quý khách vui lòng thanh toán đúng hạn để đảm bảo quyền lợi của mình.<br>
            Nếu đã thanh toán, vui lòng bỏ qua email này.
        </p>

        <p style="font-size:14px; color:#4a5568; text-align:center;">
            Mọi thắc mắc xin liên hệ: <strong>Hotline 1900 1234</strong> hoặc 
            <a href="mailto:support@emob.vn" style="color:#3182ce;">support@emob.vn</a>.
        </p>
        """, cusName, totalAmount, deposit, termMonths, monthlyAmount, nextDueDate);
    }

    private String remindInstallmentOverdue(
            String cusName,
            BigDecimal monthlyAmount,
            LocalDate nextDueDate
    ) {
        return String.format("""
            <p style="color:#e53e3e; font-size:16px; text-align:center;">
                Thông báo quá hạn thanh toán!
            </p>
            <p style="color:#2d3748; font-size:15px; line-height:1.6; text-align:center;">
                Kính gửi <strong>%s</strong>,<br>
                Quý khách đã chưa thanh toán kỳ trả góp cho đơn hàng 
                đến hạn vào ngày <strong>%s</strong>.
            </p>
            <p style="font-size:14px; color:#4a5568; text-align:center;">
                Số tiền cần thanh toán: <strong style="color:#e53e3e;">%,.0f VND</strong>.<br>
                Vui lòng thanh toán sớm để tránh phí trễ hạn hoặc bị ngừng hỗ trợ hợp đồng.
            </p>
            """, cusName, nextDueDate, monthlyAmount);

    }

    public BigDecimal calculateMonthlyAmount(BigDecimal totalAmount,
                                             BigDecimal deposit,
                                             float interestRate,
                                             int termMonths) {

        // tổng tiền cần trả góp = tổng - tiền đặt cọc
        BigDecimal principal = totalAmount.subtract(deposit);

        // lãi suất tháng (chia theo số tháng người dùng nhập)
        BigDecimal monthlyRate = BigDecimal.valueOf(interestRate)
                .divide(BigDecimal.valueOf(termMonths), 10, RoundingMode.HALF_UP)
                .divide(BigDecimal.valueOf(100), 10, RoundingMode.HALF_UP);

        // nếu không có lãi (interestRate = 0)
        if (monthlyRate.compareTo(BigDecimal.ZERO) == 0) {
            return principal
                    .divide(BigDecimal.valueOf(termMonths), 2, RoundingMode.HALF_UP);
        }

        // (1 + r)^n
        BigDecimal onePlusRatePowN = monthlyRate.add(BigDecimal.ONE)
                .pow(termMonths);

        // tử số = r * (1 + r)^n
        BigDecimal numerator = monthlyRate.multiply(onePlusRatePowN);

        // mẫu số = (1 + r)^n - 1
        BigDecimal denominator = onePlusRatePowN.subtract(BigDecimal.ONE);

        // công thức EMI = P * [r(1+r)^n / ((1+r)^n - 1)]
        BigDecimal monthlyPayment = principal.multiply(numerator)
                .divide(denominator, 10, RoundingMode.HALF_UP);

        // làm tròn 2 chữ số sau dấu phẩy (vd: 12345.67)
        return monthlyPayment.setScale(2, RoundingMode.HALF_UP);
    }


    @Override
    public APIResponse<InstallmentResponse> createInstallment(InstallmentRequest request) {
        if (request.getTermMonths() <= 0) {
            throw new GlobalException(ErrorCode.INVALID_TERM);
        }
        SaleOrder order = saleOrderRepository.findById(request.getOrderId())
                .filter((item) -> PaymentStatus.INSTALLMENT.equals(item.getPaymentStatus()))
                .orElseThrow(() -> new GlobalException(ErrorCode.NOT_FOUND));
        BigDecimal monthlyAmount = calculateMonthlyAmount(request.getTotalAmount(), request.getDeposit(),
                request.getInterestRate(), request.getTermMonths());
        try {
            InstallmentPlan installmentPlan = installmentPlanMapper.toInstallmentPlan(request);
            installmentPlan.setMonthlyAmount(monthlyAmount);
            installmentPlan.setDownDate(LocalDateTime.now());
            installmentPlan.setNextDueDate(LocalDate.now().plusMonths(1)); // sau 1 tháng
            installmentPlan.setStatus(InstallmentStatus.NOT_PAID);
            installmentPlan.setSaleOrder(order);
            installmentPlanRepository.save(installmentPlan);
            String content = sendInstallmentCreatedEmail(installmentPlan.getSaleOrder().getCustomer().getFullName(),
                    installmentPlan.getTotalAmount(), installmentPlan.getDeposit(),
                    installmentPlan.getMonthlyAmount(), installmentPlan.getTermMonths(),
                    installmentPlan.getNextDueDate());
            emailService.sendEmail(
                    "Xác nhận kế hoạch trả góp",
                    "Kế hoạch trả góp đã được tạo thành công",
                    "Cảm ơn quý khách đã tin tưởng Showroom Ô Tô EMOB",
                    NotificationHelper.INSTALLMENT_CREATED,
                    "Thông tin chi tiết về kế hoạch trả góp của bạn",
                    "",
                    content,
                    "Chúng tôi rất vui được phục vụ bạn!",
                    installmentPlan.getSaleOrder().getCustomer().getFullName(),
                    "Xem chi tiết hợp đồng",
                    installmentPlan.getSaleOrder().getCustomer().getEmail()
            );

            InstallmentResponse response = installmentPlanMapper.toInstallmentResponse(installmentPlan);
            return APIResponse.success(response, "Create installment plan successfully");
        } catch (DataIntegrityViolationException ex) {
            throw new GlobalException(ErrorCode.DATA_INVALID);
        } catch (DataAccessException ex) {
            throw new GlobalException(ErrorCode.DB_ERROR);
        } catch (Exception ex) {
            System.out.println("Exception: " + ex.getMessage());
            throw new GlobalException(ErrorCode.OTHER);
        }
    }

    @Override
    public APIResponse<InstallmentResponse> updateInstallmentByStatus(UUID id, InstallmentStatus status) {
        InstallmentPlan installmentPlan = installmentPlanRepository.findById(id)
                .filter((item) -> item.getStatus().equals(InstallmentStatus.NOT_PAID))
                .orElseThrow(() -> new GlobalException(ErrorCode.NOT_FOUND));
        installmentPlan.setStatus(InstallmentStatus.PAID);
        installmentPlan.setNextDueDate(null);
        installmentPlan.setUpdateAt(LocalDateTime.now());
        installmentPlanRepository.save(installmentPlan);
        InstallmentResponse installmentResponse = installmentPlanMapper.toInstallmentResponse(installmentPlan);
        return APIResponse.success(installmentResponse, "Update Installment Successfully");
    }

    @Override
    public APIResponse<InstallmentResponse> viewInstallmentPlan(UUID id) {
        InstallmentPlan installmentPlan = installmentPlanRepository.findById(id)
                .orElseThrow(() -> new GlobalException(ErrorCode.NOT_FOUND));
        InstallmentResponse response = installmentPlanMapper.toInstallmentResponse(installmentPlan);
        return APIResponse.success(response, "View Installment Successfully");
    }

    @Override
    public APIResponse<PageResponse<InstallmentResponse>> viewAllInstallmentPlans(Pageable pageable) {
        Page<InstallmentPlan> planPage = installmentPlanRepository.findAll(pageable);
        PageResponse<InstallmentResponse> response = pageMapper.toPageResponse(planPage, installmentPlanMapper::toInstallmentResponse);
        return APIResponse.success(response, "View All Installment Plan Successfully");
    }
}
