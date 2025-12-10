package com.infynno.javastartup.startup.modules.auth.repository;

import java.time.Instant;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import com.infynno.javastartup.startup.modules.auth.model.OtpRecord;
import com.infynno.javastartup.startup.modules.auth.model.User;

public interface OtpRecordRepository extends JpaRepository<OtpRecord, Long> {
    Optional<OtpRecord> findByCodeAndUsedFalseAndExpiresAtAfter(String code, Instant now);

    Optional<OtpRecord> findTopByUserAndPurposeAndUsedFalseAndExpiresAtAfterOrderByCreatedAtDesc(
            User user, String purpose, Instant now);

    void deleteAllByExpiresAtBefore(Instant now);
}
