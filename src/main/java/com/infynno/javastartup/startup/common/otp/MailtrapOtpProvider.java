package com.infynno.javastartup.startup.common.otp;

import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.infynno.javastartup.startup.common.exceptions.AuthException;

import io.mailtrap.client.MailtrapClient;
import io.mailtrap.config.MailtrapConfig;
import io.mailtrap.factory.MailtrapClientFactory;
import io.mailtrap.model.request.emails.Address;
import io.mailtrap.model.request.emails.MailtrapMail;

@Component
public class MailtrapOtpProvider implements OtpProvider {

    @Value("${mailtrap.api-key}")
    private String apiKey;

    @Value("${mailtrap.sender-email}")
    private String senderEmail;

    @Value("${mailtrap.sender-name}")
    private String senderName;

    private volatile MailtrapClient mailtrapClient;

    @Override
    public String getName() {
        return "MAILTRAP";
    }

    private MailtrapClient getClient() {
        if (mailtrapClient == null) {
            synchronized (this) {
                if (mailtrapClient == null) {
                    MailtrapConfig config = new MailtrapConfig.Builder()
                        .token(apiKey)
                        .build();

                    mailtrapClient =
                        MailtrapClientFactory.createMailtrapClient(config);
                }
            }
        }
        return mailtrapClient;
    }

    @Override
    public void sendOtp(String toEmail, String otp, String purpose) {

        String subject = switch (purpose) {
            case "EMAIL_VERIFICATION" -> "Verify Your Email Address";
            case "PASSWORD_RESET" -> "Reset Your Password";
            default -> "Your OTP Code";
        };

        String html = """
            <div style="font-family: Arial, sans-serif; max-width: 600px; margin: auto;
                        padding: 20px; border: 1px solid #ddd; border-radius: 10px;">
                <h2>%s</h2>
                <p>Your OTP is:</p>
                <div style="font-size: 32px; font-weight: bold; letter-spacing: 6px;
                            text-align: center; background: #F3F4F6;
                            padding: 20px; border-radius: 8px; margin: 20px 0;">
                    %s
                </div>
            </div>
        """.formatted(subject, otp);

        MailtrapMail mail = MailtrapMail.builder()
            .from(new Address(senderEmail, senderName))
            .to(List.of(new Address(toEmail)))
            .subject(subject)
            .html(html)
            .category(purpose)
            .build();

        try {
            getClient().send(mail);
        } catch (Exception e) {
            System.err.println("Failed to send OTP via Mailtrap: " + e.getMessage());
            throw new AuthException("Failed to send OTP via Mailtrap");
        }
    }
}
