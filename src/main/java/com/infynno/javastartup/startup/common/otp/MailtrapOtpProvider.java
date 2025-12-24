package com.infynno.javastartup.startup.common.otp;

import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import com.infynno.javastartup.startup.common.exceptions.AuthException;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class MailtrapOtpProvider implements OtpProvider {
    
    private final RestTemplate restTemplate = new RestTemplate();

    @Value("${otp.provider.mailtrap.api-key}")
    private String apiKey;

    @Value("${otp.provider.mailtrap.inbox-id}")
    private String inboxId;

    @Value("${otp.provider.mailtrap.sender-email}")
    private String senderEmail;

    @Value("${otp.provider.mailtrap.sender-name}")
    private String senderName;

    @Override
    public String getName() {
        return "MAILTRAP";
    }

    @Override
    public void sendOtp(String toEmail, String otp, String purpose) {
        String subject = switch (purpose) {
            case "EMAIL_VERIFICATION" -> "Verify Your Email Address";
            case "PASSWORD_RESET" -> "Your Password Reset OTP";
            default -> "Your OTP Code";
        };

        String html = """
                <div style="font-family: Arial, sans-serif; max-width: 600px; margin: auto; padding: 20px; border: 1px solid #ddd; border-radius: 10px;">
                    <h2 style="color: #4F46E5;">%s</h2>
                    <p>Here is your verification code:</p>
                    <div style="font-size: 32px; font-weight: bold; letter-spacing: 8px; color: #1F2937; text-align: center; background: #F3F4F6; padding: 20px; border-radius: 8px; margin: 20px 0;">
                        %s
                    </div>
                    <p>This code expires in <strong>10 minutes</strong>.</p>
                    <p>If you didn't request this, please ignore this email.</p>
                </div>
                """.formatted(subject.replace("Your ", ""), otp);

        Map<String, Object> body = Map.of(
                "from", Map.of("email", senderEmail, "name", senderName),
                "to", List.of(Map.of("email", toEmail)),
                "subject", subject,
                "html", html,
                "category", purpose.toLowerCase()
        );

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(apiKey);

        try {
            restTemplate.postForEntity(
                    "https://send.api.mailtrap.io/api/send/" + inboxId,
                    new HttpEntity<>(body, headers),
                    String.class
            );
        } catch (Exception e) {
            throw new AuthException("Failed to send OTP email. Please try again later.");
        }
    }
}