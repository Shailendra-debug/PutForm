package com.skushwaha.getform.Auth;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

@Component

public class JwtUtil {

    private final SecretKey secretKey;
    private final long expiration;

    public JwtUtil(
            @Value("${jwt.secret}") String secret,
            @Value("${jwt.access-expiration}") long expiration
    ) {

        if (secret == null || secret.isBlank()) {
            throw new IllegalStateException(
                    "JWT secret must not be empty"
            );
        }

        try {
            byte[] keyBytes = Decoders.BASE64.decode(secret);

            if (keyBytes.length < 32) {
                throw new IllegalStateException(
                        "JWT secret must contain at least 256 bits (32 bytes)"
                );
            }

            this.secretKey = Keys.hmacShaKeyFor(keyBytes);

        } catch (IllegalArgumentException e) {
            throw new IllegalStateException(
                    "JWT secret must be a valid Base64 encoded value",
                    e
            );
        }

        if (expiration <= 0) {
            throw new IllegalStateException(
                    "JWT access expiration must be greater than 0"
            );
        }

        this.expiration = expiration;
    }
    // Generate JWT
    public String generateToken(UserDetails userDetails) {
        System.out.println("Hello from generateToken");

        Date now = new Date();

        Date expiryDate =
                new Date(now.getTime() + expiration);

        return Jwts.builder()
                .subject(userDetails.getUsername())
                .issuedAt(now)
                .expiration(expiryDate)
                .signWith(secretKey)
                .compact();
    }

    // Extract username/email
    public String extractUsername(String token) {

        return extractAllClaims(token)
                .getSubject();
    }

    // Extract all claims
    private Claims extractAllClaims(String token) {

        return Jwts.parser()
                .verifyWith(secretKey)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    // Check expiration
    public boolean isTokenExpired(String token) {

        return extractAllClaims(token)
                .getExpiration()
                .before(new Date());
    }

    // Validate JWT
    public boolean isTokenValid(
            String token,
            UserDetails userDetails
    ) {

        try {

            String username =
                    extractUsername(token);

            return username.equals(
                    userDetails.getUsername()
            )
                    && !isTokenExpired(token);

        } catch (Exception e) {

            return false;
        }
    }

}
