package com.infynno.javastartup.startup.modules.auth.service;

import java.time.Instant;
import java.util.List;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import com.infynno.javastartup.startup.common.exceptions.AuthException;
import com.infynno.javastartup.startup.modules.auth.config.JwtService;
import com.infynno.javastartup.startup.modules.auth.model.RefreshToken;
import com.infynno.javastartup.startup.modules.auth.model.User;
import com.infynno.javastartup.startup.modules.auth.repository.RefreshTokenRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class RefreshTokenService {
    @Autowired
    private final RefreshTokenRepository repository;
    private final JwtService jwtService;

    @Value("${jwt.refresh-expiration-ms}")
    private long refreshExpiryMs;

    @Transactional
    public RefreshToken create(User user) {
        String family = UUID.randomUUID().toString();
        String tokenStr = jwtService.generateRefreshToken(user.getEmail(), family);
        Instant now = Instant.now();
        RefreshToken rt = RefreshToken.builder().token(tokenStr).user(user).createdAt(now)
                .expiresAt(now.plusMillis(refreshExpiryMs)).family(family).build();

        return repository.save(rt);
    }

    public RefreshToken findByToken(String token) {
        return repository.findByToken(token).orElse(null);
    }

    @Transactional
    public RefreshToken rotate(RefreshToken old) throws AuthException {
        if (old.isRevoked() || old.isExpired()) {
            throw AuthException.invalidRefreshToken();
        }

        String family = jwtService.extractFamily(old.getToken());
        if (old.getReplacedBy() != null) {
            revokeFamily(old.getUser(), family, "theft_detected");
            throw AuthException.tokenTheftDetected();
        }

        old.setRevokedAt(Instant.now());
        old.setRevokedReason("rotated");
        repository.save(old);

        String newTokenStr = jwtService.generateRefreshToken(old.getUser().getEmail(), family);
        RefreshToken newRt = RefreshToken.builder().token(newTokenStr).user(old.getUser())
                .createdAt(Instant.now()).expiresAt(Instant.now().plusMillis(refreshExpiryMs))
                .family(family).build();
        RefreshToken savedNew = repository.save(newRt);

        old.setReplacedBy(savedNew.getToken());
        repository.save(old);

        return savedNew;
    }

    @Transactional
    public void revoke(RefreshToken token, String reason) {
        if (token != null && !token.isRevoked()) {
            token.setRevokedAt(Instant.now());
            token.setRevokedReason(reason);
            repository.save(token);
        }
    }

    @Transactional
    public void revokeAllForUser(User user, String reason) {
        List<RefreshToken> tokens = repository.findAllByUser(user);
        Instant now = Instant.now();
        tokens.forEach(t -> {
            t.setRevokedAt(now);
            t.setRevokedReason(reason);
        });
        repository.saveAll(tokens);
        user.setTokenInvalidBefore(now);
    }

    private void revokeFamily(User user, String family, String reason) {
        List<RefreshToken> familyTokens = repository.findByUserAndFamily(user, family);
        Instant now = Instant.now();
        familyTokens.forEach(t -> {
            t.setRevokedAt(now);
            t.setRevokedReason(reason);
        });
        repository.saveAll(familyTokens);
        user.setTokenInvalidBefore(now);
    }
}
