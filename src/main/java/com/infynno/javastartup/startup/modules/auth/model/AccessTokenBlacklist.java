package com.infynno.javastartup.startup.modules.auth.model;

import java.time.Instant;
import org.hibernate.annotations.GenericGenerator;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "access_token_blacklist",
        indexes = {@Index(name = "idx_access_token_jti", columnList = "jti")})

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AccessTokenBlacklist {
    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(name = "UUID", strategy = "org.hibernate.id.UUIDGenerator")
    @Column(name = "id", updatable = false, nullable = false, columnDefinition = "VARCHAR(36)")
    private String id;

    @Column(unique = true, nullable = false)
    private String jti;


    @Column(nullable = false)
    private Instant expiredAt;

    @Column(nullable = false)
    private Instant createdAt;

    @Column(length = 512)
    private String reason;
}
