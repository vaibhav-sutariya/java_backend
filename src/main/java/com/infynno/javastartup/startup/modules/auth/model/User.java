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
@Table(name = "users",
        indexes = {@Index(name = "idx_user_email", columnList = "email"),
                @Index(name = "idx_user_phone", columnList = "phoneNumber"),
                @Index(name = "idx_user_token_invalid", columnList = "token_invalid_before")})
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
    @Column(name = "email_verified", nullable = false)
    @lombok.Builder.Default
    private boolean emailVerified = false;

    @Column(name = "email_verified_at")
    private Instant emailVerifiedAt;

    @Column(name = "email_verification_sent_at")
    private Instant emailVerificationSentAt;

    @Column(unique = true, length = 15)
    private String phoneNumber;

    @Column(length = 500)
    private String businessName;

    @Column(length = 500)
    private String businessAddress;

    @Column(length = 100)
    private String city;

    @Column(length = 100)
    private String state;

    @Column(length = 20)
    private String zipCode;

    @Column(unique = true, length = 15)
    private String gstNumber;

    @Column(columnDefinition = "TEXT", length = 5000)
    private String notes;

    @Column(length = 500)
    private String profileImage;

    @Column(columnDefinition = "TEXT", length = 10000)
    private String signature;

    @Column(length = 6)
    private String pin;

    @Column(nullable = true)
    private BigDecimal gstPercentage;
}
