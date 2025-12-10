package com.infynno.javastartup.startup.common.utils;

import java.util.Map;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import com.infynno.javastartup.startup.common.exceptions.AuthException;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class MailtrapService {
    private final RestTemplate restTemplate = new RestTemplate();

    @Value("${mailtrap.api-key}")
    private String apiKey;

    @Value("${mailtrap.inbox-id}")
    private String inboxId;

    @Value("${mailtrap.sender-email}")
    private String senderEmail;

    @Value("${mailtrap.sender-name}")
    private String senderName;

    public void sendOtpEmail(String toEmail, String otp) {
        String url = "https://send.api.mailtrap.io/api/send/" + inboxId;

        HttpHeaders headers = new HttpHeaders();
        headers.add("Accept", "application/json");
        headers.setContentType(org.springframework.http.MediaType.APPLICATION_JSON);
        headers.setBearerAuth(apiKey);

        String htmlBody = """
                <div style="font-family: Arial, sans-serif; padding: 20px; text-align: center;">
                    <h2>Password Reset OTP</h2>
                    <p>Your OTP is:</p>
                    <h1 style="font-size: 48px; letter-spacing: 10px; color: #4F46E5;">%s</h1>
                    <p>This OTP will expire in 10 minutes.</p>
                    <p>If you didn't request this, ignore this email.</p>
                </div>
                """.formatted(otp);

        Map<String, Object> body = Map.of("from", Map.of("email", senderEmail, "name", senderName),
                "to", java.util.List.of(Map.of("email", toEmail)), "subject",
                "Your Password Reset OTP", "html", htmlBody, "category", "password-reset");

        HttpEntity<Map<String, Object>> request = new HttpEntity<>(body, headers);

        try {
            restTemplate.postForEntity(url, request, String.class);
        } catch (Exception e) {
            throw new AuthException("Failed to send OTP email. Please try again.");
        }
    }
}
