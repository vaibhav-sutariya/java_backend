package com.infynno.javastartup.startup.modules.auth.model;

import java.time.Instant;
import org.hibernate.annotations.GenericGenerator;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "otp_records",
        indexes = {@Index(name = "idx_otp_code", columnList = "code"),
                @Index(name = "idx_otp_user_purpose", columnList = "user_id,purpose"),
                @Index(name = "idz_otp_user_expires", columnList = "user_id, expires_at")})

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OtpRecord {

    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(name = "UUID", strategy = "org.hibernate.id.UUIDGenerator")
    @Column(name = "id", updatable = false, nullable = false, columnDefinition = "VARCHAR(36)")
    private String id;

    @Column(nullable = false, length = 10)
    private String code;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false)
    private Instant createdAt;

    @Column(nullable = false)
    private Instant expiresAt;

    @Column(nullable = false)
    private boolean used;

    @Column(length = 50)
    private String purpose;

    @Column(length = 50)
    private String channel;
}
