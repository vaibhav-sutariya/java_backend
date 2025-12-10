package com.infynno.javastartup.startup.modules.auth.repository;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import com.infynno.javastartup.startup.modules.auth.model.OtpRecord;
import com.infynno.javastartup.startup.modules.auth.model.User;

public interface OtpRecordRepository extends JpaRepository<OtpRecord, Long> {
    Optional<OtpRecord> findByCodeAndPurposeAndUsedFalseAndExpiresAtAfter(String code,
            String purpose, Instant now);

    List<OtpRecord> findByUserAndPurposeAndCreatedAtAfter(User user, String purpose, Instant since);

    long countByUserAndPurposeAndCreatedAtAfter(User user, String purpose, Instant now);

    void deleteAllByExpiresAtBefore(Instant now);
}
