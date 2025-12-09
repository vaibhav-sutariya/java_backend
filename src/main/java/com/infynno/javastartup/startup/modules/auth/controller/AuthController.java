package com.infynno.javastartup.startup.modules.auth.controller;

import java.time.Instant;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.infynno.javastartup.startup.config.JwtService;
import com.infynno.javastartup.startup.modules.auth.dto.AuthResponse;
import com.infynno.javastartup.startup.modules.auth.dto.LoginRequest;
import com.infynno.javastartup.startup.modules.auth.dto.RefreshTokenRequest;
import com.infynno.javastartup.startup.modules.auth.dto.RegisterRequest;
import com.infynno.javastartup.startup.modules.auth.model.User;
import com.infynno.javastartup.startup.modules.auth.repository.UserRepository;
import com.infynno.javastartup.startup.modules.auth.service.AccessTokenBlacklistService;
import com.infynno.javastartup.startup.modules.auth.service.AuthService;
import jakarta.security.auth.message.AuthException;
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

    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@Valid @RequestBody RegisterRequest req)
            throws AuthException {
        return ResponseEntity.ok(authService.register(req));
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest req)
            throws AuthException {
        return ResponseEntity.ok(authService.login(req));
    }

    @PostMapping("/refresh")
    public ResponseEntity<AuthResponse> refresh(@Valid @RequestBody RefreshTokenRequest req)
            throws AuthException {
        return ResponseEntity.ok(authService.refresh(req.getRefreshToken()));
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(@RequestHeader("Authorization") String authHeader,
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
        return ResponseEntity.ok().build();
    }

    @PostMapping("/logout-all")
    public ResponseEntity<Void> logoutAll(@RequestHeader("Authorization") String authHeader)
            throws AuthException {
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

        return ResponseEntity.ok().build();
    }


}
