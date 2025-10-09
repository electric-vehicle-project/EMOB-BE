/* EMOB-2025 */
package com.example.emob.service;

import com.example.emob.entity.TestDrive;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.hibernate.query.sqm.tree.SqmNode;
import org.springframework.mail.MailException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

@Service
@RequiredArgsConstructor
public class NotificationService {

  private final JavaMailSender mailSender;
  private final TemplateEngine templateEngine;

  public void sendTestDriveConfirmation(TestDrive testDrive) {
    // Build context cho Thymeleaf
    Context context = new Context();
    context.setVariable("customerName", testDrive.getCustomer().getFullName());
    context.setVariable("scheduleTime", testDrive.getScheduledAt().toString());
    context.setVariable("location", testDrive.getLocation());
    context.setVariable("staffName", testDrive.getSalesperson().getFullName());

    // Render HTML từ template
    String htmlContent = templateEngine.process("email/test-drive-confirmation", context);

    try {
      MimeMessage message = mailSender.createMimeMessage();
      MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

      helper.setTo(testDrive.getCustomer().getEmail());
      helper.setSubject("Xác nhận lịch lái thử xe");
      helper.setText(htmlContent, true); // true = HTML

      mailSender.send(message);
    } catch (MessagingException e) {
      SqmNode.log.error("Lỗi tạo email: {}", e.getMessage(), e);
      throw new RuntimeException("Lỗi tạo email: " + e.getMessage(), e);
    } catch (MailException e) {
      SqmNode.log.error("Lỗi gửi email: {}", e.getMessage(), e);
      throw new RuntimeException("Lỗi gửi email: " + e.getMessage(), e);
    }
  }
}
