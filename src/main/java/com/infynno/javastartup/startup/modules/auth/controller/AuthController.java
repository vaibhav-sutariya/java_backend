package com.infynno.javastartup.startup.modules.auth.controller;

import java.time.Instant;
import java.util.Map;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.infynno.javastartup.startup.common.exceptions.AuthException;
import com.infynno.javastartup.startup.common.response.ApiResponse;
import com.infynno.javastartup.startup.modules.auth.config.JwtService;
import com.infynno.javastartup.startup.modules.auth.dto.AuthResponse;
import com.infynno.javastartup.startup.modules.auth.dto.ForgotPasswordRequest;
import com.infynno.javastartup.startup.modules.auth.dto.LoginRequest;
import com.infynno.javastartup.startup.modules.auth.dto.RefreshTokenRequest;
import com.infynno.javastartup.startup.modules.auth.dto.RegisterRequest;
import com.infynno.javastartup.startup.modules.auth.dto.RegisterResponse;
import com.infynno.javastartup.startup.modules.auth.dto.ResetPasswordRequest;
import com.infynno.javastartup.startup.modules.auth.dto.VerifyEmailRequest;
import com.infynno.javastartup.startup.modules.auth.dto.VerifyOtpRequest;
import com.infynno.javastartup.startup.modules.auth.model.User;
import com.infynno.javastartup.startup.modules.auth.repository.UserRepository;
import com.infynno.javastartup.startup.modules.auth.service.AccessTokenBlacklistService;
import com.infynno.javastartup.startup.modules.auth.service.AuthService;
import com.infynno.javastartup.startup.modules.auth.service.OtpService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;


@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;
    private final JwtService jwtService;
    private final AccessTokenBlacklistService blacklistService;
    private final UserRepository userRepo;
    private final OtpService otpService;

    @PostMapping("/register")
    public ResponseEntity<ApiResponse<RegisterResponse>> register(
            @Valid @RequestBody RegisterRequest req) throws AuthException {
        return ResponseEntity
                .ok(ApiResponse.success("Registration successful", authService.register(req)));
    }

    @PostMapping("/verify-email")
    public ResponseEntity<ApiResponse<AuthResponse>> verifyEmail(
            @Valid @RequestBody VerifyEmailRequest req) throws AuthException {
        return ResponseEntity.ok(ApiResponse.success("Email pending verification",
                authService.verifyEmailAndLogin(req)));
    }

    @PostMapping("/resend-verification")
    public ResponseEntity<ApiResponse<Map<String, String>>> resendVerification(
            @RequestBody Map<String, String> request) throws AuthException {
        String email = request.get("email");
        if (email == null || email.isBlank()) {
            throw new AuthException("Email is required");
        }

        authService.resendVerificationOtp(email);
        return ResponseEntity.ok(ApiResponse.success("Verification OTP sent successfully",
                Map.of("message", "Verification OTP sent successfully", "timestamp",
                        Instant.now().toString())));
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<AuthResponse>> login(@Valid @RequestBody LoginRequest req)
            throws AuthException {
        return ResponseEntity.ok(ApiResponse.success("Login successful", authService.login(req)));
    }

    @PostMapping("/refresh")
    public ResponseEntity<ApiResponse<AuthResponse>> refresh(
            @Valid @RequestBody RefreshTokenRequest req) throws AuthException {
        return ResponseEntity.ok(
                ApiResponse.success("Token refreshed", authService.refresh(req.getRefreshToken())));
    }

    @PostMapping("/logout")
    public ResponseEntity<ApiResponse<Void>> logout(
            @RequestHeader("Authorization") String authHeader,
            @RequestBody RefreshTokenRequest req) {
        if (req.getRefreshToken() != null) {
            authService.logout(req.getRefreshToken());
        }
        if (authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7);
            if (jwtService.isValid(token)) {
                String jti = jwtService.extractJti(token);
                Instant exp = jwtService.extractExpiration(token);
                blacklistService.blacklist(jti, exp, "logout");
            }
        }
        return ResponseEntity.ok(ApiResponse.success("Logged out successfully"));
    }

    @PostMapping("/logout-all")
    public ResponseEntity<ApiResponse<Void>> logoutAll(
            @RequestHeader("Authorization") String authHeader) throws AuthException {
        if (!authHeader.startsWith("Bearer ")) {
            throw new AuthException("Missing token");
        }
        String token = authHeader.substring(7);
        if (!jwtService.isValid(token)) {
            throw new AuthException("Invalid token");
        }
        String email = jwtService.extractEmail(token);
        User user =
                userRepo.findByEmail(email).orElseThrow(() -> new AuthException("User not found"));

        authService.logoutAllDevices(user);

        String jti = jwtService.extractJti(token);
        Instant exp = jwtService.extractExpiration(token);
        blacklistService.blacklist(jti, exp, "logout_all");

        return ResponseEntity.ok(ApiResponse.success("Logged out from all devices"));
    }

    @PostMapping("/forgot-password")
    public ResponseEntity<ApiResponse<Map<String, String>>> forgotPassword(
            @Valid @RequestBody ForgotPasswordRequest req) {
        otpService.sendOtp(req.getEmail(), "PASSWORD_RESET");
        return ResponseEntity.ok(ApiResponse.success("OTP sent",
                Map.of("message", "OTP sent to your email (check Mailtrap inbox)")));
    }

    @PostMapping("/verify-otp")
    public ResponseEntity<ApiResponse<Map<String, String>>> verifyOtp(
            @Valid @RequestBody VerifyOtpRequest req) {
        otpService.verifyOtp(req.getEmail(), req.getOtp(), "PASSWORD_RESET");
        return ResponseEntity.ok(ApiResponse.success("OTP verified",
                Map.of("message", "OTP verified successfully")));
    }

    @PostMapping("/reset-password")
    public ResponseEntity<ApiResponse<Map<String, String>>> resetPassword(
            @Valid @RequestBody ResetPasswordRequest req) {
        authService.resetPassword(req.getEmail(), req.getOldPassword(), req.getNewPassword());
        return ResponseEntity.ok(ApiResponse.success("Password reset",
                Map.of("message", "Password reset successfully")));
    }


}
