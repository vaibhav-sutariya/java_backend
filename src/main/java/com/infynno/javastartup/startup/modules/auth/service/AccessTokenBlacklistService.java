package com.infynno.javastartup.startup.modules.auth.service;

import java.time.Instant;
import org.springframework.stereotype.Service;
import com.infynno.javastartup.startup.modules.auth.model.AccessTokenBlacklist;
import com.infynno.javastartup.startup.modules.auth.repository.AccessTokenBlacklistRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AccessTokenBlacklistService {
    private final AccessTokenBlacklistRepository repository;

    @Transactional
    public void blacklist(String jti, Instant expiresAt, String reason) {
        if (repository.findByJti(jti).isPresent())
            return; // Idempotent
        AccessTokenBlacklist entry = AccessTokenBlacklist.builder().jti(jti).expiredAt(expiresAt)
                .createdAt(Instant.now()).reason(reason).build();
        repository.save(entry);
    }

    public boolean isBlacklisted(String jti) {
        return repository.findByJti(jti).isPresent();
    }

    @Transactional
    public void cleanupExpired() {
        repository.deleteAllByExpiredAtBefore(Instant.now());
    }

}
