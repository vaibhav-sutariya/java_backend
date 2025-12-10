package com.infynno.javastartup.startup.modules.auth.service;

import java.security.SecureRandom;
import java.time.Instant;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import com.infynno.javastartup.startup.common.exceptions.AuthException;
import com.infynno.javastartup.startup.common.utils.MailtrapService;
import com.infynno.javastartup.startup.modules.auth.model.OtpRecord;
import com.infynno.javastartup.startup.modules.auth.model.User;
import com.infynno.javastartup.startup.modules.auth.repository.OtpRecordRepository;
import com.infynno.javastartup.startup.modules.auth.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class OtpService {

    private final UserRepository userRepository;
    private final OtpRecordRepository otpRepository;
    private final PasswordEncoder passwordEncoder;
    private final MailtrapService mailtrapService;

    @Value("${otp.expiry-minutes:10}")
    private int expiryMinutes;

    @Value("${otp.length:6}")
    private int otpLength;

    private static final String PURPOSE_RESET = "PASSWORD_RESET";

    @Transactional
    public void sendForgotPasswordOtp(String email) throws AuthException {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new AuthException("No Account found with this email"));

        String otp = generateOtp();
        Instant now = Instant.now();
        Instant expiresAt = now.plusSeconds(expiryMinutes * 60L);


        OtpRecord otpRecord = OtpRecord.builder().code(otp).user(user).purpose(PURPOSE_RESET)
                .createdAt(now).expiresAt(expiresAt).used(false).build();
        otpRepository.save(otpRecord);

        mailtrapService.sendOtpEmail(email, otp);
    }

    @Transactional
    public void veirfyOtp(String email, String otp) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new AuthException("Invalid email"));
        Instant now = Instant.now();

        OtpRecord record = otpRepository.findByCodeAndUsedFalseAndExpiresAtAfter(otp, now)
                .orElseThrow(() -> new AuthException("Invalid or expired OTP"));

        if (!record.getUser().equals(user)) {
            throw new AuthException("Invalid OTP for the provided email");
        }

        if (!PURPOSE_RESET.equals(record.getPurpose())) {
            throw new AuthException("Invalid OTP purpose");
        }

        record.setUsed(true);
        otpRepository.save(record);
    }



    private String generateOtp() {
        int max = (int) Math.pow(10, otpLength) - 1;
        return String.format("%0" + otpLength + "d", new SecureRandom().nextInt(max));
    }

    @Scheduled(cron = "0 0 * * * *") // Runs every hour
    public void cleanupExpiredOtps() {
        otpRepository.deleteAllByExpiresAtBefore(Instant.now());
    }

}
