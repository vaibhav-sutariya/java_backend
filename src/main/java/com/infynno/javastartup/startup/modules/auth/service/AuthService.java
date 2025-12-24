package com.infynno.javastartup.startup.modules.auth.service;

import java.time.Instant;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import com.infynno.javastartup.startup.common.exceptions.AuthException;
import com.infynno.javastartup.startup.modules.auth.config.JwtService;
import com.infynno.javastartup.startup.modules.auth.dto.AuthResponse;
import com.infynno.javastartup.startup.modules.auth.dto.LoginRequest;
import com.infynno.javastartup.startup.modules.auth.dto.RegisterRequest;
import com.infynno.javastartup.startup.modules.auth.dto.RegisterResponse;
import com.infynno.javastartup.startup.modules.auth.dto.VerifyEmailRequest;
import com.infynno.javastartup.startup.modules.auth.model.RefreshToken;
import com.infynno.javastartup.startup.modules.auth.model.Role;
import com.infynno.javastartup.startup.modules.auth.model.User;
import com.infynno.javastartup.startup.modules.auth.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final UserRepository userRepo;
    private final JwtService jwtService;
    private final PasswordEncoder encoder;
    private final RefreshTokenService refreshService;
    private final OtpService otpService;

    @Value("${jwt.access-expiration-ms}")
    private long accessExpiryMs;


    @Transactional
    public RegisterResponse register(RegisterRequest req) throws AuthException {
        if (userRepo.existsByEmail(req.getEmail())) {
            throw AuthException.emailAlreadyExists();
        }
        User user = User.builder().name(req.getName()).email(req.getEmail())
                .password(encoder.encode(req.getPassword())).role(Role.USER).emailVerified(false).emailVerificationSentAt(Instant.now()).build();
        userRepo.save(user);

        // RefreshToken rt = refreshService.create(user);
        // String access = jwtService.generateAccessToken(user.getEmail(), user.getRole());
        otpService.sendOtp(req.getEmail(),"EMAIL_VERIFICATION" );

        return new RegisterResponse(user.getId(),
            user.getEmail(),
            user.getName(),
            "Registration successful. Please check your email for verification OTP.");
    }

    @Transactional
    public  AuthResponse verifyEmailAndLogin(VerifyEmailRequest req) throws  AuthException{
        otpService.verifyOtp(req.getEmail(), req.getOtp(), "EMAIL_VERIFICATION");

        User user = userRepo.findByEmail(req.getEmail()).orElseThrow(()-> new AuthException("User not found"));

        user.setEmailVerified(true);
        user.setEmailVerifiedAt(Instant.now());
        userRepo.save(user);

        RefreshToken rt = refreshService.create(user);
        String access = jwtService.generateAccessToken(user.getEmail(), user.getRole());

        return new AuthResponse(access, rt.getToken(), "Bearer", accessExpiryMs / 1000);
    }

    @Transactional
    public AuthResponse login(LoginRequest req) throws AuthException {
        User user = userRepo.findByEmail(req.getEmail())
                .orElseThrow(() -> new AuthException("User not found"));
        if (!encoder.matches(req.getPassword(), user.getPassword())) {
            throw AuthException.invalidCredentials();
        }

        // Check if email is verified
        if (!user.isEmailVerified()) {
            throw new AuthException(
                "Please verify your email first. Check your inbox for verification OTP.",
                HttpStatus.FORBIDDEN
            );
        }

        RefreshToken rt = refreshService.create(user);
        String access = jwtService.generateAccessToken(user.getEmail(), user.getRole());

        return new AuthResponse(access, rt.getToken(), "Bearer", accessExpiryMs / 1000);
    }

    @Transactional
    public  void resendVerificationOtp(String email) throws  AuthException{
        User user = userRepo.findByEmail(email)
                .orElseThrow(() -> new AuthException("User not found"));

        if (user.isEmailVerified()) {
            throw new AuthException("Email is already verified.");
        }

        // Check rate limiting (max 3 attempts per hour)
        Instant oneHourAgo = Instant.now().minusSeconds(3600);
        if (user.getEmailVerificationSentAt() != null && 
            user.getEmailVerificationSentAt().isAfter(oneHourAgo)) {
            throw new AuthException(
                "Verification OTP already sent. Please wait before requesting another.",
                HttpStatus.TOO_MANY_REQUESTS
            );
        }

        user.setEmailVerificationSentAt(Instant.now());
        userRepo.save(user);

        otpService.sendOtp(email, "EMAIL_VERIFICATION");
    }   

    @Transactional
    public AuthResponse refresh(String refreshToken) throws AuthException {
        if (refreshToken == null || refreshToken.isBlank()) {
            throw AuthException.refreshTokenRequired();
        }

        RefreshToken rt = refreshService.findByToken(refreshToken);
        if (rt == null || rt.isRevoked() || rt.isExpired()) {
            throw AuthException.invalidRefreshToken();
        }

        RefreshToken newRt = refreshService.rotate(rt);
        User user = rt.getUser();
        String newAccess = jwtService.generateAccessToken(user.getEmail(), user.getRole());

        return new AuthResponse(newAccess, newRt.getToken(), "Bearer", accessExpiryMs / 1000);
    }

    @Transactional
    public void logout(String refreshTokenStr) {
        RefreshToken persisted = refreshService.findByToken(refreshTokenStr);
        if (persisted != null) {
            refreshService.revoke(persisted, "logout");
        }
    }

    @Transactional
    public void logoutAllDevices(User user) {
        refreshService.revokeAllForUser(user, "logout_all");
    }

    @Transactional
    public void resetPassword(String email, String oldPassword, String newPassword) {
        User user =
                userRepo.findByEmail(email).orElseThrow(() -> new AuthException("Invalid Email"));

        if (!encoder.matches(oldPassword, user.getPassword())) {
            throw new AuthException("Old password is incorrect");
        }

        user.setPassword(encoder.encode(newPassword));
        user.setTokenInvalidBefore(Instant.now());
        userRepo.save(user);
    }
}
