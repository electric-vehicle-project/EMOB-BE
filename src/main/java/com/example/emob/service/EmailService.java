/* EMOB-2025 */
package com.example.emob.service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.hibernate.query.sqm.tree.SqmNode;
import org.springframework.core.io.ClassPathResource;
import org.springframework.mail.MailException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import com.example.emob.service.impl.IEmail;

import java.io.UnsupportedEncodingException;

@Service
@RequiredArgsConstructor
public class EmailService implements IEmail {

    private final JavaMailSender mailSender;
    private final TemplateEngine templateEngine;

    @Override
    public void sendEmail(String headerType ,String title, String subTitle, String icon, String greeting, String buttonUrl,
                                                   String content, String alert, String customer, String buttonName, String toEmail) {
        Context context = new Context();
        context.setVariable("headerType", headerType);
        context.setVariable("headerIcon", icon);
        context.setVariable("headerTitle", title);
        context.setVariable("headerSubTitle", subTitle);
        context.setVariable("content", content);
        context.setVariable("alertMessage", alert);
        context.setVariable("customerName", customer);
        context.setVariable("showGreeting", greeting);
        context.setVariable("buttons", buttonName);
        context.setVariable("buttonUrl", buttonUrl);

        // Render HTML từ template
        String htmlContent = templateEngine.process("email/notification-customer", context);

        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            // giúp hiển thị logo
            ClassPathResource logo = new ClassPathResource("templates/email/logo_swp-removebg-preview.png");
            helper.addInline("logoImage", logo);



            // thông tin người gửi
            helper.setFrom("cn100705@gmail.com", "Showroom EMOB");
            helper.setTo(toEmail);
            helper.setSubject(headerType);
            helper.setText(htmlContent, true); // true = HTML

            mailSender.send(message);
            System.out.println("✅ Email đã gửi thành công đến: " + toEmail);
        } catch (MessagingException e) {
            SqmNode.log.error("Lỗi tạo email: {}", e.getMessage(), e);
            throw new RuntimeException("Lỗi tạo email: " + e.getMessage(), e);
        } catch (MailException e) {
            SqmNode .log.error("Lỗi gửi email: {}", e.getMessage(), e);
            throw new RuntimeException("Lỗi gửi email: " + e.getMessage(), e);
        } catch (UnsupportedEncodingException e) {
            SqmNode .log.error("Lỗi người gửi: {}", e.getMessage(), e);
            throw new RuntimeException(e);
        }
    }
}
