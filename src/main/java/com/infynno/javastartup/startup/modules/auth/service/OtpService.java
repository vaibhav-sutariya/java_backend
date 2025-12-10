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

    @Value("${otp.daily-limit:5}")
    private int dailyLimit;

    private static final String PURPOSE_RESET = "PASSWORD_RESET";

    @Transactional
    public void sendOtp(String email, String purpose) throws AuthException {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new AuthException("No Account found with this email"));

        // Rate limit: 5 per day
        Instant startOfDay = Instant.now().truncatedTo(java.time.temporal.ChronoUnit.DAYS);
        long todayCount =
                otpRepository.countByUserAndPurposeAndCreatedAtAfter(user, purpose, startOfDay);

        if (todayCount >= dailyLimit) {
            throw new AuthException("Too many OTP requests. Try again tomorrow.");
        }

        String otp = generateOtp();
        Instant expiresAt = Instant.now().plusSeconds(expiryMinutes * 60L);


        OtpRecord record = OtpRecord.builder().code(otp).user(user).purpose(purpose)
                .createdAt(Instant.now()).expiresAt(expiresAt).used(false).channel("EMAIL").build();
        otpRepository.save(record);

        mailtrapService.sendOtp(user.getEmail(), otp, purpose);
    }

    @Transactional
    public void verifyOtp(String email, String otp, String purpose) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new AuthException("Invalid email"));
        Instant now = Instant.now();

        OtpRecord record =
                otpRepository.findByCodeAndPurposeAndUsedFalseAndExpiresAtAfter(otp, purpose, now)
                        .orElseThrow(() -> new AuthException("Invalid or expired OTP"));

        if (!record.getUser().equals(user)) {
            throw new AuthException("Invalid OTP for the provided email");
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
