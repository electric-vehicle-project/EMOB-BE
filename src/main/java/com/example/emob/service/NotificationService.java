/* EMOB-2025 */
package com.example.emob.service;

import com.example.emob.entity.InstallmentPlan;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ClassPathResource;
import org.springframework.mail.MailException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.math.BigDecimal;
import java.time.LocalDate;

@Slf4j
@Service
@RequiredArgsConstructor
public class NotificationService {

  private final JavaMailSender mailSender;
  private final TemplateEngine templateEngine;

  public void sendInstallmentCreatedEmail(
          String recipientEmail,
          String cusName,
          BigDecimal totalAmount,
          BigDecimal deposit,
          BigDecimal monthlyAmount,
          int termMonths,
          LocalDate nextDueDate) {

    // Build context cho Thymeleaf
    Context context = new Context();

    // Header section
    context.setVariable("emailTitle", "Thông báo kế hoạch trả góp");
    context.setVariable("headerIcon", "📋");
    context.setVariable("headerTitle", "Kế hoạch trả góp đã được tạo");
    context.setVariable("headerSubtitle", "Chi tiết thông tin trả góp của bạn");

    // Greeting section
    context.setVariable("greetingText", "Xin chào");
    context.setVariable("customerName", cusName);
    context.setVariable(
            "greetingMessage",
            "Kế hoạch trả góp cho đơn hàng của bạn đã được khởi tạo thành công");

    // Details section
    context.setVariable("detailsTitle", "📄 Chi tiết kế hoạch trả góp:");
    context.setVariable("labelTotalAmount", "Tổng giá trị:");
    context.setVariable("labelDeposit", "Tiền đặt cọc:");
    context.setVariable("labelTermMonths", "Thời hạn:");
    context.setVariable("labelMonthlyAmount", "Số tiền trả mỗi tháng:");
    context.setVariable("labelNextDueDate", "Kỳ thanh toán đầu tiên:");

    // Data values
    context.setVariable("totalAmount", totalAmount);
    context.setVariable("deposit", deposit);
    context.setVariable("monthlyAmount", monthlyAmount);
    context.setVariable("termMonths", termMonths);
    context.setVariable("nextDueDate", nextDueDate);

    // Payment reminder
    context.setVariable(
            "paymentReminder",
            "Quý khách vui lòng thanh toán đúng hạn để đảm bảo quyền lợi của mình. "
            + "Nếu đã thanh toán, vui lòng bỏ qua email này.");

    // Alert box
    context.setVariable("alertMessage", "Vui lòng thanh toán đúng hạn.");
    context.setVariable("hotline", "Hotline 1900 1234");
    context.setVariable("supportEmail", "support@emob.vn");

    // Button
    context.setVariable("buttonText", "Xem chi tiết đơn hàng");
    context.setVariable("buttonUrl", "https://emob.vn/my-orders");

    // Footer
    context.setVariable("footerSupportText", "Bạn cần hỗ trợ?");
    context.setVariable("footerPhone", "1900 1234");
    context.setVariable("footerEmail", "support@emob.vn");
    context.setVariable("footerCopyright", "© 2025 Showroom Ô Tô EMOB");

    // Render HTML từ template
    String htmlContent = templateEngine.process("email/installment-plan", context);

    try {
      MimeMessage message = mailSender.createMimeMessage();
      MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

      helper.setTo(recipientEmail);
      helper.setSubject("Thông báo kế hoạch trả góp - EMOB");
      helper.setText(htmlContent, true); // true = HTML

      // Add logo inline
      ClassPathResource logoResource = new ClassPathResource("static/images/logo.png");
      helper.addInline("logoImage", logoResource);

      mailSender.send(message);

      log.info("Đã gửi email kế hoạch trả góp thành công tới: {}", recipientEmail);
    } catch (MessagingException e) {
      log.error("Lỗi tạo email: {}", e.getMessage(), e);
      throw new RuntimeException("Lỗi tạo email: " + e.getMessage(), e);
    } catch (MailException e) {
      log.error("Lỗi gửi email: {}", e.getMessage(), e);
      throw new RuntimeException("Lỗi gửi email: " + e.getMessage(), e);
    }
  }

  // Overload method để gọi từ entity Installment
  public void sendInstallmentCreatedEmail(InstallmentPlan installment) {
    sendInstallmentCreatedEmail(
            installment.getSaleOrder().getQuotation().getCustomer().getEmail(),
            installment.getSaleOrder().getQuotation().getCustomer().getFullName(),
            installment.getTotalAmount(),
            installment.getDeposit(),
            installment.getMonthlyAmount(),
            installment.getTermMonths(),
            installment.getNextDueDate());
  }
}