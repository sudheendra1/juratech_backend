
package com.juratech.backend.service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    private final JavaMailSender mailSender;

    // Pulls your sending email address from application.properties
    @Value("${spring.mail.username}")
    private String fromEmail;

    // The URL of your React frontend
    @Value("${app.frontend.url:http://localhost:3000}")
    private String frontendUrl;

    public EmailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    public void sendWelcomeEmail(String toEmail, String token) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setFrom(fromEmail);
            helper.setTo(toEmail);
            helper.setSubject("Welcome to Juratech - Set Up Your Account");

            // The link that points back to your React app's password reset module
            String resetLink = frontendUrl + "/force-reset?token=" + token;

            // Build a clean HTML email
            String htmlContent = "<div style='font-family: Arial, sans-serif; padding: 20px; color: #333;'>"
                    + "<h2 style='color: #1a365d;'>Welcome to Juratech!</h2>"
                    + "<p>An administrator has created an account for you.</p>"
                    + "<p>To get started and access your dashboard, you need to set up a secure password.</p>"
                    + "<div style='margin: 30px 0;'>"
                    + "<a href='" + resetLink + "' style='background-color: #2563eb; color: white; padding: 12px 24px; text-decoration: none; border-radius: 4px; font-weight: bold;'>Set My Password</a>"
                    + "</div>"
                    + "<p style='font-size: 12px; color: #666;'>If the button doesn't work, copy and paste this link into your browser:<br>" + resetLink + "</p>"
                    + "</div>";

            helper.setText(htmlContent, true); // Set to 'true' to enable HTML parsing

            mailSender.send(message);
            System.out.println("Welcome email successfully sent to " + toEmail);

        } catch (MessagingException e) {
            System.err.println("Failed to send welcome email to " + toEmail);
            e.printStackTrace();
        }
    }
}