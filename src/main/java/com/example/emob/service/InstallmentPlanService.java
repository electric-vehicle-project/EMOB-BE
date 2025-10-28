/* EMOB-2025 */
package com.example.emob.service;

import com.example.emob.constant.ErrorCode;
import com.example.emob.constant.InstallmentStatus;
import com.example.emob.constant.PaymentStatus;
import com.example.emob.entity.Customer;
import com.example.emob.entity.Dealer;
import com.example.emob.entity.InstallmentPlan;
import com.example.emob.entity.SaleOrder;
import com.example.emob.exception.GlobalException;
import com.example.emob.mapper.InstallmentPlanMapper;
import com.example.emob.mapper.PageMapper;
import com.example.emob.model.request.installment.InstallmentRequest;
import com.example.emob.model.response.APIResponse;
import com.example.emob.model.response.InstallmentResponse;
import com.example.emob.model.response.PageResponse;
import com.example.emob.repository.CustomerRepository;
import com.example.emob.repository.InstallmentPlanRepository;
import com.example.emob.repository.SaleOrderRepository;
import com.example.emob.service.impl.IInstallmentPlan;
import com.example.emob.util.AccountUtil;
import com.example.emob.util.NotificationHelper;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class InstallmentPlanService implements IInstallmentPlan {
  @Autowired InstallmentPlanMapper installmentPlanMapper;

  @Autowired SaleOrderRepository saleOrderRepository;

  @Autowired InstallmentPlanRepository installmentPlanRepository;

  @Autowired PageMapper pageMapper;

  @Autowired NotificationService emailService;

  @Autowired EmailService sendEmail;
  @Autowired CustomerRepository customerRepository;

  //    @Scheduled(cron = "0 0 8 * * *") // mỗi ngày 8h sẽ chạy tự dộng
  @Scheduled(cron = "0 0 0 * * *")
  public void remindOverdueDaily() {
    LocalDate today = LocalDate.now();
    // quá hạn nhưng chưa nhắc hôm nay
    List<InstallmentPlan> plans = installmentPlanRepository.findAllOverdueNeedingReminder(today);

    for (InstallmentPlan p : plans) {
      // Cập nhật trạng thái nếu chưa set OVERDUE
      if (p.getNextDueDate().isBefore(today) && p.getStatus() != InstallmentStatus.PAID) {
        p.setStatus(InstallmentStatus.OVERDUE);
      }
      Customer customer = p.getSaleOrder().getQuotation().getCustomer();

      if (customer != null) {
        // Gửi email nhắc quá hạn
        String content =
            remindInstallmentOverdue(
                customer.getFullName(), p.getMonthlyAmount(), p.getNextDueDate());
        sendEmail.sendEmail(
            "Thông báo quá hạn thanh toán đơn hàng ",
            "Quá hạn thanh toán trả góp",
            "Thanh toán hợp đồng trả góp bị trễ hạn",
            NotificationHelper.INSTALLMENT_OVERDUE,
            "Vui lòng thanh toán ngay để tránh bị tính phí trễ hạn.",
            "",
            content,
            "Nếu đã thanh toán, vui lòng bỏ qua email này.",
            customer.getFullName(),
            "Thanh toán ngay",
            customer.getEmail());
      }

      // cấm gửi trùng trong ngày
      p.setLastReminderDate(today);
      p.setReminderCount((p.getReminderCount() == null ? 0 : p.getReminderCount()) + 1);
      p.setUpdateAt(LocalDateTime.now());
      installmentPlanRepository.save(p);
    }
  }

  public void createInstallmentPlanFromEntity(InstallmentPlan installment) {
    emailService.sendInstallmentCreatedEmail(installment);
  }

  private String remindInstallmentOverdue(
      String cusName, BigDecimal monthlyAmount, LocalDate nextDueDate) {
    return String.format(
        """
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
            """,
        cusName, nextDueDate, monthlyAmount);
  }

  public BigDecimal calculateMonthlyAmount(
      BigDecimal deposit, int termMonths, float interestRate, BigDecimal totalPrice) {

    // ✅ Tiền gốc cần trả góp = tổng giá - tiền đặt cọc
    BigDecimal principal = totalPrice.subtract(deposit);

    // ✅ Lãi suất hàng tháng (từ % sang thập phân)
    BigDecimal monthlyRate =
        BigDecimal.valueOf(interestRate)
            .divide(BigDecimal.valueOf(100), 10, RoundingMode.HALF_UP)
            .divide(BigDecimal.valueOf(12), 10, RoundingMode.HALF_UP); // lãi suất năm chia 12 tháng

    // ✅ Nếu không có lãi suất
    if (monthlyRate.compareTo(BigDecimal.ZERO) == 0) {
      return principal.divide(BigDecimal.valueOf(termMonths), 2, RoundingMode.HALF_UP);
    }

    // ✅ (1 + r)^n
    BigDecimal onePlusRatePowN = monthlyRate.add(BigDecimal.ONE).pow(termMonths);

    // ✅ Công thức EMI = P * [r(1+r)^n / ((1+r)^n - 1)]
    BigDecimal numerator = monthlyRate.multiply(onePlusRatePowN);
    BigDecimal denominator = onePlusRatePowN.subtract(BigDecimal.ONE);

    BigDecimal monthlyPayment =
        principal.multiply(numerator).divide(denominator, 10, RoundingMode.HALF_UP);

    // ✅ Làm tròn 2 chữ số thập phân
    return monthlyPayment.setScale(2, RoundingMode.HALF_UP);
  }

  @Override
  @Transactional
  public APIResponse<InstallmentResponse> createInstallment(InstallmentRequest request) {
    if (request.getTermMonths() <= 0) {
      throw new GlobalException(ErrorCode.INVALID_TERM);
    }
    SaleOrder order =
        saleOrderRepository
            .findById(request.getOrderId())
            .filter((item) -> PaymentStatus.INSTALLMENT.equals(item.getPaymentStatus()))
            .orElseThrow(() -> new GlobalException(ErrorCode.NOT_FOUND));
    BigDecimal monthlyAmount =
        calculateMonthlyAmount(
            request.getDeposit(),
            request.getTermMonths(),
            request.getInterestRate(),
            order.getTotalPrice());
    try {
      InstallmentPlan installmentPlan = installmentPlanMapper.toInstallmentPlan(request);
      installmentPlan.setMonthlyAmount(monthlyAmount);
      installmentPlan.setDownDate(LocalDateTime.now());
      installmentPlan.setNextDueDate(LocalDate.now().plusMonths(1)); // sau 1 tháng
      installmentPlan.setStatus(InstallmentStatus.NOT_PAID);
      installmentPlan.setSaleOrder(order);
      installmentPlanRepository.save(installmentPlan);
      Customer customer = null;
      if (order != null && order.getQuotation() != null) {
        customer = order.getQuotation().getCustomer();
      }
      if (customer != null) {
        createInstallmentPlanFromEntity(installmentPlan);
      }
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
  public APIResponse<InstallmentResponse> updateInstallmentByStatus(
      UUID id, InstallmentStatus status) {
    InstallmentPlan installmentPlan =
        installmentPlanRepository
            .findById(id)
            .filter((item) -> item.getStatus().equals(InstallmentStatus.NOT_PAID))
            .orElseThrow(() -> new GlobalException(ErrorCode.NOT_FOUND));
    installmentPlan.setStatus(InstallmentStatus.PAID);
    installmentPlan.setNextDueDate(null);
    installmentPlan.setUpdateAt(LocalDateTime.now());
    installmentPlanRepository.save(installmentPlan);
    InstallmentResponse installmentResponse =
        installmentPlanMapper.toInstallmentResponse(installmentPlan);
    return APIResponse.success(installmentResponse, "Update Installment Successfully");
  }

  @Override
  public APIResponse<InstallmentResponse> viewInstallmentPlan(UUID id) {
    InstallmentPlan installmentPlan =
        installmentPlanRepository
            .findById(id)
            .orElseThrow(() -> new GlobalException(ErrorCode.NOT_FOUND));
    InstallmentResponse response = installmentPlanMapper.toInstallmentResponse(installmentPlan);
    return APIResponse.success(response, "View Installment Successfully");
  }

  // ============================================================
  // 🔹 1. Hãng xe (EVM_STAFF, ADMIN) xem tất cả plan của các đại lý
  // ============================================================
  @Override
  @PreAuthorize("hasAnyRole('EVM_STAFF', 'ADMIN')")
  public APIResponse<PageResponse<InstallmentResponse>> getAllPlansOfDealers(
      List<InstallmentStatus> statuses, Pageable pageable) {

    Page<InstallmentPlan> page =
        installmentPlanRepository.findAllWithVehicleRequest(statuses, pageable);
    PageResponse<InstallmentResponse> response =
        pageMapper.toPageResponse(page, installmentPlanMapper::toInstallmentResponse);
    return APIResponse.success(response);
  }

  // ============================================================
  // 🔹 2. Đại lý (MANAGER, DEALER_STAFF) xem các plan của chính đại lý mình
  // ============================================================
  @Override
  @PreAuthorize("hasAnyRole('DEALER_STAFF', 'MANAGER')")
  public APIResponse<PageResponse<InstallmentResponse>> getAllPlansOfCurrentDealer(
      List<InstallmentStatus> statuses, Pageable pageable) {

    Dealer dealer = AccountUtil.getCurrentUser().getDealer();
    Page<InstallmentPlan> page =
        installmentPlanRepository.findAllWithVehicleRequestByDealerAndStatuses(
            dealer, statuses, pageable);
    PageResponse<InstallmentResponse> response =
        pageMapper.toPageResponse(page, installmentPlanMapper::toInstallmentResponse);
    return APIResponse.success(response);
  }

  // ============================================================
  // 🔹 3. Đại lý xem các plan đã báo giá cho khách hàng cụ thể
  // ============================================================
  @Override
  @PreAuthorize("hasAnyRole('DEALER_STAFF', 'MANAGER')")
  public APIResponse<PageResponse<InstallmentResponse>> getAllPlansOfCurrentCustomer(
      UUID customerId, List<InstallmentStatus> statuses, Pageable pageable) {

    Dealer dealer = AccountUtil.getCurrentUser().getDealer();
    Customer customer =
        customerRepository
            .findById(customerId)
            .orElseThrow(() -> new GlobalException(ErrorCode.NOT_FOUND, "Customer not found"));

    Page<InstallmentPlan> page =
        installmentPlanRepository.findAllWithQuotationByDealerAndCustomer(
            dealer, customer, statuses, pageable);
    PageResponse<InstallmentResponse> response =
        pageMapper.toPageResponse(page, installmentPlanMapper::toInstallmentResponse);
    return APIResponse.success(response);
  }

  // ============================================================
  // 🔹 4. Đại lý xem tất cả plan đã báo giá (mọi khách hàng)
  // ============================================================
  @Override
  @PreAuthorize("hasAnyRole('DEALER_STAFF', 'MANAGER')")
  public APIResponse<PageResponse<InstallmentResponse>> getAllPlansByCustomer(
      List<InstallmentStatus> statuses, Pageable pageable) {

    Dealer dealer = AccountUtil.getCurrentUser().getDealer();
    Page<InstallmentPlan> page =
        installmentPlanRepository.findAllWithQuotationByDealerAndStatuses(
            dealer, statuses, pageable);
    PageResponse<InstallmentResponse> response =
        pageMapper.toPageResponse(page, installmentPlanMapper::toInstallmentResponse);
    return APIResponse.success(response);
  }
}
