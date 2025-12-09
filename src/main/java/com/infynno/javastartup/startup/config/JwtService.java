package com.infynno.javastartup.startup.config;

import java.security.Key;
import java.time.Instant;
import java.util.Date;
import java.util.UUID;
import java.util.function.Function;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import com.infynno.javastartup.startup.modules.auth.model.Role;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;

@Service
public class JwtService {

    @Value("${jwt.secret}")
    private String secret;

    @Value("${jwt.access-expiration-ms}")
    private long accessExpiryMs;

    @Value("${jwt.refresh-expiration-ms}")
    private long refreshExpiryMs;

    private Key key;

    @PostConstruct
    public void init() {
        this.key = Keys.hmacShaKeyFor(secret.getBytes());
    }

    // ACCESS TOKEN
    public String generateAccessToken(String email, Role role) {
        String jti = UUID.randomUUID().toString();
        return Jwts.builder().setId(jti).setSubject(email).claim("role", role)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + accessExpiryMs))
                .signWith(key, SignatureAlgorithm.HS256).compact();
    }

    // REFRESH TOKEN
    public String generateRefreshToken(String email, String family) {
        return Jwts.builder().setSubject(email).claim("family", family).setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + refreshExpiryMs))
                .signWith(key, SignatureAlgorithm.HS256).compact();
    }

    // === EXTRACTORS ===
    private Claims extractAllClaims(String token) {
        return Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token).getBody();
    }

    private <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    public String extractEmail(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    public String extractJti(String token) {
        return extractClaim(token, Claims::getId);
    }

    public String extractRole(String token) {
        return extractClaim(token, claims -> claims.get("role", String.class));
    }

    public String extractFamily(String token) {
        return extractClaim(token, claims -> claims.get("family", String.class));
    }

    public Instant extractIssuedAt(String token) {
        return extractClaim(token, claims -> claims.getIssuedAt().toInstant());
    }

    public Instant extractExpiration(String token) {
        return extractClaim(token, claims -> claims.getExpiration().toInstant());
    }

    public boolean isValid(String token) {
        try {
            extractAllClaims(token);
            return !extractExpiration(token).isBefore(java.time.Instant.now());
        } catch (Exception e) {
            return false;
        }
    }
}
