package com.infynno.javastartup.startup.modules.auth.model;

import java.math.BigDecimal;
import java.time.Instant;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.infynno.javastartup.startup.common.model.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

@Entity
@Getter
@Setter
@ToString(callSuper = true)
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "users", indexes = {@Index(name = "idx_user_email", columnList = "email"),
        @Index(name = "idx_user_phone", columnList = "phoneNumber")})
public class User extends BaseEntity {

    // ID inherited from BaseEntity

    @Column(nullable = false)
    private String name;

    @Column(unique = true, nullable = false)
    private String email;

    @JsonIgnore
    @Column(nullable = false)
    private String password;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role;

    @Column(name = "token_invalid_before")
    private Instant tokenInvalidBefore;

    // New fields for email verification
    @Column(name = "email_verified", nullable = true)
    @lombok.Builder.Default
    private boolean emailVerified = false;

    @Column(name = "email_verified_at")
    private Instant emailVerifiedAt;

    @Column(name = "email_verification_sent_at")
    private Instant emailVerificationSentAt;

    @Column(nullable = true, unique = true)
    private String phoneNumber;

    @Column(nullable = true)
    private String businessName;

    @Column(nullable = true)
    private String businessAddress;

    @Column(nullable = true)
    private String city;

    @Column(nullable = true)
    private String state;

    @Column(nullable = true)
    private String zipCode;

    @Column(nullable = true, unique = true)
    private String gstNumber;

    @Column(columnDefinition = "TEXT")
    private String notes;

    @Column(nullable = true)
    private String profileImage;

    @Column(columnDefinition = "TEXT")
    private String signature;

    @Column(nullable = true)
    private String pin;

    @Column(nullable = true)
    private BigDecimal gstPercentage;
}
