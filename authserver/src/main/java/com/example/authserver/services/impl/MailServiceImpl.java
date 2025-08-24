package com.example.authserver.services.impl;

import com.example.authserver.services.MailService;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

/**
 * Implementation of the {@link MailService} interface for sending email notifications.
 * <p>
 * This service is responsible for sending verification emails to newly registered users.
 * Emails are sent asynchronously to avoid blocking the main thread.
 * </p>
 */
@Slf4j
@Service
@AllArgsConstructor
public class MailServiceImpl implements MailService {

    private final JavaMailSender mailSender;

    /**
     * Sends a verification email to the specified email address containing a link
     * with a verification token.
     * <p>
     * The email is sent asynchronously. The link directs the user to the verification
     * endpoint of the application. If sending the email fails, the exception is logged.
     * </p>
     *
     * @param email the recipient's email address
     * @param token the verification token to include in the email link
     */
    @Async
    public void sendVerificationMail(String email, String token) {

        try {

            String subject = "Please verify your email";
            String confirmationUrl = "https://127.0.0.1:8443/verify?token=" + token;

            String message = """
                    <html>
                      <body style="font-family: Arial, sans-serif; line-height:1.6; color:#333;">
                        <h2 style="color:#2c3e50;">Welcome to <span style="color:#007bff;">SecureLogin</span>!</h2>
                        <p>Thanks for registering. Please confirm your email address by clicking the button below:</p>
                        <p style="text-align:center; margin:20px 0;">
                          <a href="%s"
                             style="background-color:#007bff; color:#fff; padding:12px 20px; text-decoration:none; border-radius:5px; font-weight:bold;">
                             Verify Email
                          </a>
                        </p>
                        <p>If the button doesn’t work, copy and paste this link into your browser:</p>
                        <p><a href="%s">%s</a></p>
                        <hr>
                        <small>This link is valid for 15 minutes. If you did not create an account, you can ignore this email.</small>
                      </body>
                    </html>
                    """.formatted(confirmationUrl, confirmationUrl, confirmationUrl);

            MimeMessage mimeMessage = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, "utf-8");
            helper.setTo(email);
            helper.setSubject(subject);
            helper.setText(message, true);

            mailSender.send(mimeMessage);
        } catch (MessagingException e) {
            log.error(e.getMessage());
        }
    }
}
