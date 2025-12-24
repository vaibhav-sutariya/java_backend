package com.infynno.javastartup.startup.common.otp;

public interface OtpProvider {
    void sendOtp(String toEmail, String otp, String purpose);
    String getName();
}
