package com.infynno.javastartup.startup.config;

import java.io.IOException;
import java.time.Instant;
import java.util.Map;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.infynno.javastartup.startup.common.exceptions.AuthException;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.security.SignatureException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class CustomAuthenticationEntryPoint implements AuthenticationEntryPoint {

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response,
            AuthenticationException authException) throws IOException {
        
        response.setContentType("application/json");
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        
        String message = determineErrorMessage(request);
        String errorType = determineErrorType(request);
        
        Map<String, Object> body = Map.of(
                "timestamp", Instant.now().toString(),
                "status", HttpStatus.UNAUTHORIZED.value(),
                "error", errorType,
                "message", message
        );
        
        ObjectMapper mapper = new ObjectMapper();
        mapper.findAndRegisterModules();
        response.getWriter().write(mapper.writeValueAsString(body));
    }
    
    private String determineErrorMessage(HttpServletRequest request) {
        // Check for JWT exceptions stored as request attributes
        Throwable exception = (Throwable) request.getAttribute("jwt.exception");
        
        if (exception != null) {
            if (exception instanceof ExpiredJwtException) {
                return "Your session has expired. Please login again.";
            } else if (exception instanceof MalformedJwtException) {
                return "Invalid token format. Please provide a valid JWT token.";
            } else if (exception instanceof SignatureException) {
                return "Invalid token signature. The token may have been tampered with.";
            } else if (exception instanceof AuthException) {
                return ((AuthException) exception).getUserMessage();
            }
        }
        
        // Check Authorization header
        String authHeader = request.getHeader("Authorization");
        if (authHeader == null || authHeader.isBlank()) {
            return "Authentication required. Please provide a Bearer token.";
        }
        
        if (!authHeader.startsWith("Bearer ")) {
            return "Invalid authorization format. Expected: 'Bearer <token>'.";
        }
        
        // Default message
        return "Authentication failed. Please check your credentials and try again.";
    }
    
    private String determineErrorType(HttpServletRequest request) {
        Throwable exception = (Throwable) request.getAttribute("jwt.exception");
        
        if (exception != null) {
            if (exception instanceof ExpiredJwtException) {
                return "Token Expired";
            } else if (exception instanceof MalformedJwtException || 
                       exception instanceof SignatureException) {
                return "Invalid Token";
            } else if (exception instanceof AuthException) {
                return "Authentication Failed";
            }
        }
        
        return "Unauthorized";
    }
}