package com.infynno.javastartup.startup.modules.auth.service;

import java.time.Instant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import com.infynno.javastartup.startup.modules.auth.model.User;
import com.infynno.javastartup.startup.modules.auth.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserService {
    @Autowired
    private final UserRepository repo;
    private final PasswordEncoder encoder;
    private final RefreshTokenService refreshService;

    @Transactional
    public void changePassword(User user, String newPassword) {
        user.setPassword(encoder.encode(newPassword));
        user.setTokenInvalidBefore(Instant.now()); // Invalidate all old tokens
        refreshService.revokeAllForUser(user, "password_change");
        repo.save(user);
    }

    // Stub for future MFA/social login
    public void enableMfa(User user) {
        // Implement TOTP or similar
    }
}
