package com.infynno.javastartup.startup.common.exceptions;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.security.SignatureException;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;

@RestControllerAdvice
public class GlobalExceptionHandler {

        private static final boolean IS_PRODUCTION = false; // ← Set to true in production!

        // 1. Your custom business logic errors (AuthException)
        @ExceptionHandler(AuthException.class)
        public ResponseEntity<Object> handleAuthException(AuthException ex) {
                Map<String, Object> body = Map.of("timestamp", Instant.now(), "status",
                                ex.getStatus().value(), "error", ex.getUserMessage());

                if (!IS_PRODUCTION && ex.getDebugMessage() != null) {
                        Map<String, Object> devBody = new HashMap<>(body);
                        devBody.put("debug", ex.getDebugMessage());
                        return new ResponseEntity<>(devBody, ex.getStatus());
                }

                return new ResponseEntity<>(body, ex.getStatus());
        }

        // 2. JWT parsing / expired / invalid signature
        @ExceptionHandler({ExpiredJwtException.class, MalformedJwtException.class,
                        SignatureException.class})
        public ResponseEntity<Object> handleJwtException(Exception ex) {
                return buildResponse(HttpStatus.UNAUTHORIZED, "Invalid or expired token");
        }

        // 3. @Valid validation failures (Register/Login DTOs)
        @ExceptionHandler(MethodArgumentNotValidException.class)
        public ResponseEntity<Object> handleValidationErrors(MethodArgumentNotValidException ex) {
                Map<String, String> errors = new HashMap<>();
                for (FieldError error : ex.getBindingResult().getFieldErrors()) {
                        errors.put(error.getField(), error.getDefaultMessage());
                }
                return buildResponse(HttpStatus.BAD_REQUEST, "Validation failed", errors);
        }

        // 4. @Validated on method params (e.g. @Email, @NotBlank on single params)
        @ExceptionHandler(ConstraintViolationException.class)
        public ResponseEntity<Object> handleConstraintViolation(ConstraintViolationException ex) {
                Map<String, String> errors = new HashMap<>();
                for (ConstraintViolation<?> violation : ex.getConstraintViolations()) {
                        String field = violation.getPropertyPath().toString();
                        errors.put(field, violation.getMessage());
                }
                return buildResponse(HttpStatus.BAD_REQUEST, "Validation failed", errors);
        }

        // 5. Any other RuntimeException (NPE, IllegalState, etc.) — DETAILED IN DEV
        @ExceptionHandler(RuntimeException.class)
        public ResponseEntity<Object> handleRuntimeException(RuntimeException ex,
                        WebRequest request) {
                if (IS_PRODUCTION) {
                        return buildResponse(HttpStatus.INTERNAL_SERVER_ERROR,
                                        "Something went wrong");
                } else {
                        return buildResponse(HttpStatus.INTERNAL_SERVER_ERROR,
                                        ex.getClass().getSimpleName() + ": " + ex.getMessage(),
                                        Map.of("stackTrace", getStackTrace(ex)));
                }
        }

        // 6. Final fallback — never expose raw exception
        @ExceptionHandler(Exception.class)
        public ResponseEntity<Object> handleAll(Exception ex, WebRequest request) {
                if (IS_PRODUCTION) {
                        return buildResponse(HttpStatus.INTERNAL_SERVER_ERROR,
                                        "An unexpected error occurred");
                } else {
                        return buildResponse(HttpStatus.INTERNAL_SERVER_ERROR,
                                        ex.getClass().getSimpleName() + ": " + ex.getMessage(),
                                        Map.of("stackTrace", getStackTrace(ex)));
                }
        }

        // Helper to build consistent JSON
        private ResponseEntity<Object> buildResponse(HttpStatus status, String message) {
                return buildResponse(status, message, null);
        }

        private ResponseEntity<Object> buildResponse(HttpStatus status, String message,
                        Object details) {
                Map<String, Object> body = Map.of("timestamp", Instant.now(), "status",
                                status.value(), "error", message);

                if (details != null) {
                        Map<String, Object> mutableBody = new HashMap<>(body);
                        if (details instanceof Map) {
                                mutableBody.put("details", details);
                        } else {
                                mutableBody.put("details", details);
                        }
                        return new ResponseEntity<>(mutableBody, new HttpHeaders(), status);
                }

                return new ResponseEntity<>(body, new HttpHeaders(), status);
        }

        // Only in dev: include stack trace
        private String[] getStackTrace(Throwable t) {
                return java.util.Arrays.stream(t.getStackTrace()).map(StackTraceElement::toString)
                                .limit(10).toArray(String[]::new);
        }
}
