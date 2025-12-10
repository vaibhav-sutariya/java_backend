package com.infynno.javastartup.startup.modules.auth.service;

import java.time.Instant;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import com.infynno.javastartup.startup.common.exceptions.AuthException;
import com.infynno.javastartup.startup.config.JwtService;
import com.infynno.javastartup.startup.modules.auth.dto.AuthResponse;
import com.infynno.javastartup.startup.modules.auth.dto.LoginRequest;
import com.infynno.javastartup.startup.modules.auth.dto.RegisterRequest;
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

    @Value("${jwt.access-expiration-ms}")
    private long accessExpiryMs;


    @Transactional
    public AuthResponse register(RegisterRequest req) throws AuthException {
        if (userRepo.existsByEmail(req.getEmail())) {
            throw AuthException.emailAlreadyExists();
        }
        User user = User.builder().name(req.getName()).email(req.getEmail())
                .password(encoder.encode(req.getPassword())).role(Role.USER).build();
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

        RefreshToken rt = refreshService.create(user);
        String access = jwtService.generateAccessToken(user.getEmail(), user.getRole());

        return new AuthResponse(access, rt.getToken(), "Bearer", accessExpiryMs / 1000);
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
