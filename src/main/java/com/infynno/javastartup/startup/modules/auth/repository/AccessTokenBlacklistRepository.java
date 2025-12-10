package com.infynno.javastartup.startup.modules.auth.repository;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import com.infynno.javastartup.startup.modules.auth.model.AccessTokenBlacklist;

public interface AccessTokenBlacklistRepository
        extends JpaRepository<AccessTokenBlacklist, String> {
    Optional<AccessTokenBlacklist> findByJti(String jti);

    void deleteAllByExpiredAtBefore(java.time.Instant now);
}
