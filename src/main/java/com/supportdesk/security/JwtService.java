package com.supportdesk.security;

import com.supportdesk.user.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

@Service
public class JwtService {

    private final SecretKey secretKey;
    private final long expiration;

    public JwtService(
            @Value("${app.jwt.secret}") String secret,
            @Value("${app.jwt.expiration}") long expiration) {
        this.secretKey = Keys.hmacShaKeyFor(
                secret.getBytes(StandardCharsets.UTF_8));
        this.expiration = expiration;
    }

    public String generateToken(User user) {
        return generateToken(user, false);
    }

    public String generateDemoToken(User user) {
        return generateToken(user, true);
    }

    private String generateToken(
            User user,
            boolean demo) {

        Date now = new Date();

        Date expiresAt = new Date(
                now.getTime() + expiration);

        var builder = Jwts.builder()
                .subject(user.getEmail())
                .claim(
                        "role",
                        user.getRole().name());

        if (demo) {
            builder.claim("demo", true);
        }

        return builder
                .issuedAt(now)
                .expiration(expiresAt)
                .signWith(secretKey)
                .compact();
    }

    public String extractEmail(String token) {
        return extractClaims(token)
                .getSubject();
    }

    public boolean isTokenValid(String token) {
        try {
            extractClaims(token);
            return true;
        } catch (Exception exception) {
            return false;
        }
    }

    public boolean isDemoToken(String token) {
        try {
            Boolean demo = extractClaims(token)
                    .get("demo", Boolean.class);

            return Boolean.TRUE.equals(demo);
        } catch (Exception exception) {
            return false;
        }
    }

    private Claims extractClaims(String token) {
        return Jwts.parser()
                .verifyWith(secretKey)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }
}