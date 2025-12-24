package com.infynno.javastartup.startup.modules.auth.service;

import java.security.SecureRandom;
import java.time.Instant;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.infynno.javastartup.startup.common.exceptions.AuthException;
import com.infynno.javastartup.startup.common.otp.OtpProvider;
import com.infynno.javastartup.startup.modules.auth.model.OtpRecord;
import com.infynno.javastartup.startup.modules.auth.model.User;
import com.infynno.javastartup.startup.modules.auth.repository.OtpRecordRepository;
import com.infynno.javastartup.startup.modules.auth.repository.UserRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class OtpService {
    
    private final UserRepository userRepository;
    private final OtpRecordRepository otpRepository;
    private final Map<String, OtpProvider> otpProviderMap;

    @Value("${otp.provider:MAILTRAP}")
    private String defaultProvider;

    @Value("${otp.expiry-minutes:10}")
    private int expiryMinutes;

    @Value("${otp.length:6}")
    private int otpLength;

    @Value("${otp.daily-limit:5}")
    private int dailyLimit;

    @Autowired
    OtpProvider provider;

    @Transactional
    public void sendOtp(String email, String purpose) throws AuthException {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new AuthException("No account found with this email"));

        // Rate limit: 5 per day per purpose
        Instant startOfDay = Instant.now().truncatedTo(java.time.temporal.ChronoUnit.DAYS);
        long todayCount = otpRepository.countByUserAndPurposeAndCreatedAtAfter(user, purpose, startOfDay);

        if (todayCount >= dailyLimit) {
            throw new AuthException("Too many OTP requests. Try again tomorrow.");
        }

        String otp = generateOtp();
        Instant expiresAt = Instant.now().plusSeconds(expiryMinutes * 60L);

        OtpRecord record = OtpRecord.builder()
                .code(otp)
                .user(user)
                .purpose(purpose)
                .createdAt(Instant.now())
                .expiresAt(expiresAt)
                .used(false)
                .channel(getProviderChannel())
                .build();
        
        otpRepository.save(record);

        try {
            provider.sendOtp(email, otp, purpose);
            log.info("OTP sent via {} to {} for purpose: {}", provider.getName(), email, purpose);
        } catch (Exception e) {
            log.error("Failed to send OTP via {}: {}", provider.getName(), e.getMessage());
            throw new AuthException("Failed to send OTP. Please try again later.");
        }
    }

    @Transactional
    public void verifyOtp(String email, String otp, String purpose) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new AuthException("Invalid email"));
        
        Instant now = Instant.now();

        OtpRecord record = otpRepository
                .findByCodeAndPurposeAndUsedFalseAndExpiresAtAfter(otp, purpose, now)
                .orElseThrow(() -> new AuthException("Invalid or expired OTP"));

        if (!record.getUser().equals(user)) {
            throw new AuthException("Invalid OTP for the provided email");
        }

        record.setUsed(true);
        otpRepository.save(record);
        
        log.info("OTP verified for user: {}, purpose: {}", email, purpose);
    }

    private String generateOtp() {
        int max = (int) Math.pow(10, otpLength) - 1;
        return String.format("%0" + otpLength + "d", new SecureRandom().nextInt(max));
    }

    private String getProviderChannel() {
        OtpProvider provider = otpProviderMap.get(defaultProvider);
        return provider != null ? provider.getName() : "UNKNOWN";
    }

    @Scheduled(cron = "0 0 * * * *") // Runs every hour
    public void cleanupExpiredOtps() {
        otpRepository.deleteAllByExpiresAtBefore(Instant.now());
        log.info("Cleaned up expired OTPs");
    }
}