package com.infynno.javastartup.startup.common.exceptions;

import org.springframework.http.HttpStatus;
import lombok.Getter;

@Getter
public class AuthException extends RuntimeException {

    private final String userMessage; // Friendly message for frontend
    private final HttpStatus status; // Proper HTTP status
    private final String debugMessage; // Optional internal detail (only in dev)

    // Most common constructor
    public AuthException(String userMessage) {
        this(userMessage, HttpStatus.UNAUTHORIZED);
    }

    public AuthException(String userMessage, HttpStatus status) {
        this(userMessage, status, null);
    }

    public AuthException(String userMessage, HttpStatus status, String debugMessage) {
        super(userMessage);
        this.userMessage = userMessage;
        this.status = status != null ? status : HttpStatus.UNAUTHORIZED;
        this.debugMessage = debugMessage;
    }

    // Static helpers — use these everywhere!
    public static AuthException invalidCredentials() {
        return new AuthException("Invalid email or password");
    }

    public static AuthException emailAlreadyExists() {
        return new AuthException("This email is already registered");
    }

    public static AuthException refreshTokenRequired() {
        return new AuthException("Refresh token is required");
    }

    public static AuthException invalidRefreshToken() {
        return new AuthException("Refresh token is invalid, expired, or revoked");
    }

    public static AuthException tokenTheftDetected() {
        return new AuthException(
                "Possible token theft detected. All sessions have been revoked for your safety.");
    }

    public static AuthException accessDenied() {
        return new AuthException("Access denied. You don't have permission.", HttpStatus.FORBIDDEN);
    }

    public static AuthException accountLocked() {
        return new AuthException("Your account is temporarily locked. Try again later.",
                HttpStatus.LOCKED);
    }
}
