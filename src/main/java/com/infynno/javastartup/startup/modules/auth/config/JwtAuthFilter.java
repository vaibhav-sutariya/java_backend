package com.infynno.javastartup.startup.modules.auth.config;

import java.io.IOException;
import java.time.Instant;
import java.util.List;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import com.infynno.javastartup.startup.common.exceptions.AuthException;
import com.infynno.javastartup.startup.modules.auth.model.User;
import com.infynno.javastartup.startup.modules.auth.repository.UserRepository;
import com.infynno.javastartup.startup.modules.auth.service.AccessTokenBlacklistService;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.security.SignatureException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@RequiredArgsConstructor
@Slf4j
public class JwtAuthFilter extends OncePerRequestFilter {
    private final JwtService jwtService;
    private final UserRepository userRepo;
    private final AccessTokenBlacklistService blacklistService;

    @Override
    protected void doFilterInternal(HttpServletRequest req, HttpServletResponse res,
            FilterChain chain) throws ServletException, IOException {
        
        String header = req.getHeader("Authorization");
        
        // Case 1: No Authorization header - let request proceed
        if (header == null || header.isBlank()) {
            chain.doFilter(req, res);
            return;
        }
        
        // Case 2: Invalid header format
        if (!header.startsWith("Bearer ")) {
            req.setAttribute("jwt.exception", 
                new MalformedJwtException("Authorization header must start with 'Bearer '"));
            chain.doFilter(req, res);
            return;
        }
        
        String token = header.substring(7);
        
        try {
            // Validate token
            if (!jwtService.isValid(token)) {
                // Try to parse to get specific exception
                try {
                    jwtService.extractAllClaims(token);
                } catch (ExpiredJwtException e) {
                    req.setAttribute("jwt.exception", e);
                    chain.doFilter(req, res);
                    return;
                } catch (MalformedJwtException | SignatureException e) {
                    req.setAttribute("jwt.exception", e);
                    chain.doFilter(req, res);
                    return;
                }
                // Generic invalid token
                req.setAttribute("jwt.exception", new MalformedJwtException("Invalid token"));
                chain.doFilter(req, res);
                return;
            }
            
            String email = jwtService.extractEmail(token);
            if (email != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                User user = userRepo.findByEmail(email).orElse(null);
                if (user != null) {
                    String jti = jwtService.extractJti(token);
                    
                    // Case: Blacklisted token
                    if (blacklistService.isBlacklisted(jti)) {
                        req.setAttribute("jwt.exception", 
                            new AuthException("This token has been revoked. Please login again."));
                        chain.doFilter(req, res);
                        return;
                    }
                    
                    Instant iat = jwtService.extractIssuedAt(token);
                    
                    // Case: Token issued before invalidation timestamp
                    if (user.getTokenInvalidBefore() != null
                            && iat.isBefore(user.getTokenInvalidBefore())) {
                        req.setAttribute("jwt.exception",
                            new AuthException("Session invalidated due to security change. Please login again."));
                        chain.doFilter(req, res);
                        return;
                    }
                    
                    String role = jwtService.extractRole(token);
                    List<SimpleGrantedAuthority> authorities =
                            List.of(new SimpleGrantedAuthority("ROLE_" + role));
                    
                    UsernamePasswordAuthenticationToken auth =
                            new UsernamePasswordAuthenticationToken(user, null, authorities);
                    auth.setDetails(new WebAuthenticationDetailsSource().buildDetails(req));
                    SecurityContextHolder.getContext().setAuthentication(auth);
                } else {
                    // Case: User not found
                    req.setAttribute("jwt.exception",
                        new AuthException("User account not found or deleted."));
                    chain.doFilter(req, res);
                    return;
                }
            }
        } catch (ExpiredJwtException e) {
            req.setAttribute("jwt.exception", e);
            chain.doFilter(req, res);
            return;
        } catch (MalformedJwtException | SignatureException e) {
            req.setAttribute("jwt.exception", e);
            chain.doFilter(req, res);
            return;
        } catch (Exception e) {
            log.error("Unexpected error during JWT authentication", e);
            req.setAttribute("jwt.exception", 
                new AuthException("Authentication error. Please try again."));
            chain.doFilter(req, res);
            return;
        }
        
        chain.doFilter(req, res);
    }
}