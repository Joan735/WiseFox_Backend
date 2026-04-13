package WiseFox.Finance.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender mailSender;

    @Value("${spring.mail.username}")
    private String from;

    public void sendVerificationCode(String toEmail, String code) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setFrom(from);
            helper.setTo(toEmail);
            helper.setSubject("WiseFox — Verification code");
            helper.setText(buildEmailHtml(code), true);

            mailSender.send(message);

        } catch (MessagingException e) {
            throw new RuntimeException("Failed to send verification email: " + e.getMessage(), e);
        }
    }

    private String buildEmailHtml(String code) {
        return """
            <div style="font-family: sans-serif; max-width: 480px; margin: 0 auto; padding: 32px;">
              <h2 style="color: #1a1a1a; margin-bottom: 8px;">Verify your email</h2>
              <p style="color: #555; margin-bottom: 24px;">
                Use the code below to complete your WiseFox registration.
                It expires in <strong>10 minutes</strong>.
              </p>
              <div style="background: #f4f4f4; border-radius: 8px; padding: 24px; text-align: center; margin-bottom: 24px;">
                <span style="font-size: 36px; font-weight: 700; letter-spacing: 10px; color: #1a1a1a;">%s</span>
              </div>
              <p style="color: #999; font-size: 13px;">
                If you didn't request this, you can safely ignore this email.
              </p>
            </div>
            """.formatted(code);
    }
}