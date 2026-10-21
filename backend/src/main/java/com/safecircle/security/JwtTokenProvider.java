package com.safecircle.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

@Component
public class JwtTokenProvider {

    private final SecretKey key;
    private final long expirationMs;

    public JwtTokenProvider(
        @Value("${safecircle.jwt.secret:404E635266556A586E3272357538782F413F4428472B4B6250645367566B5970}") String secret,
        @Value("${safecircle.jwt.expiration-ms:86400000}") long expirationMs
    ) {
        this.key = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
        this.expirationMs = expirationMs;
    }

    public String generateToken(Long userId, String phoneNumber, String name) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + expirationMs);

        return Jwts.builder()
            .subject(phoneNumber)
            .claim("userId", userId)
            .claim("name", name)
            .issuedAt(now)
            .expiration(expiryDate)
            .signWith(key)
            .compact();
    }

    public Long getUserIdFromToken(String token) {
        Claims claims = getClaims(token);
        Object userIdClaim = claims.get("userId");
        if (userIdClaim instanceof Number) {
            return ((Number) userIdClaim).longValue();
        }
        return Long.parseLong(userIdClaim.toString());
    }

    public String getPhoneNumberFromToken(String token) {
        return getClaims(token).getSubject();
    }

    public boolean validateToken(String token) {
        try {
            getClaims(token);
            return true;
        } catch (JwtException | IllegalArgumentException ex) {
            return false;
        }
    }

    private Claims getClaims(String token) {
        return Jwts.parser()
            .verifyWith(key)
            .build()
            .parseSignedClaims(token)
            .getPayload();
    }
}
