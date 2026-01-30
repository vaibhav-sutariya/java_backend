package com.infynno.javastartup.startup.modules.auth.model;

/**
 * Enum for OTP purposes to replace magic strings
 */
public enum OtpPurpose {
    EMAIL_VERIFICATION("Verify Your Email Address"), PASSWORD_RESET("Reset Your Password");

    private final String displayName;

    OtpPurpose(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
