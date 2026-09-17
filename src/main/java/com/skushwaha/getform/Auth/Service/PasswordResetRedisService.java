package com.skushwaha.getform.Auth.Service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.Duration;
import java.util.Locale;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PasswordResetRedisService {

    private final RedisTemplate<String, String> redisTemplate;

    private static final Duration OTP_EXPIRY =
            Duration.ofMinutes(10);

    private static final Duration RESET_TOKEN_EXPIRY =
            Duration.ofMinutes(10);

    private static final String OTP_PREFIX =
            "password-reset:otp:";

    private static final String RESET_PREFIX =
            "password-reset:token:";


    // ==========================================
    // SAVE OTP HASH
    // ==========================================

    public void saveOtp(
            String email,
            String otpHash
    ) {

        String key =
                OTP_PREFIX + normalize(email);

        if (!StringUtils.hasText(otpHash)) {
            throw new IllegalArgumentException(
                    "OTP hash cannot be empty"
            );
        }

        redisTemplate.opsForValue().set(
                key,
                otpHash,
                OTP_EXPIRY
        );
    }


    // ==========================================
    // GET OTP HASH
    // ==========================================

    public String getOtpHash(
            String email
    ) {

        String key =
                OTP_PREFIX + normalize(email);

        return redisTemplate.opsForValue()
                .get(key);
    }


    // ==========================================
    // DELETE OTP
    // ==========================================

    public void deleteOtp(
            String email
    ) {

        String key =
                OTP_PREFIX + normalize(email);

        redisTemplate.delete(key);
    }


    // ==========================================
    // CREATE RESET TOKEN
    // ==========================================

    public String createResetToken(
            String email
    ) {

        String token =
                UUID.randomUUID().toString();

        String key =
                RESET_PREFIX + token;

        redisTemplate.opsForValue().set(
                key,
                normalize(email),
                RESET_TOKEN_EXPIRY
        );

        return token;
    }


    // ==========================================
    // GET EMAIL FROM RESET TOKEN
    // ==========================================

    public String getEmailFromResetToken(
            String token
    ) {

        if (!StringUtils.hasText(token)) {
            return null;
        }

        String key =
                RESET_PREFIX + token.trim();

        return redisTemplate.opsForValue()
                .get(key);
    }


    // ==========================================
    // DELETE RESET TOKEN
    // ==========================================

    public void deleteResetToken(
            String token
    ) {

        if (!StringUtils.hasText(token)) {
            return;
        }

        String key =
                RESET_PREFIX + token.trim();

        redisTemplate.delete(key);
    }


    // ==========================================
    // NORMALIZE EMAIL
    // ==========================================

    private String normalize(
            String email
    ) {

        if (!StringUtils.hasText(email)) {
            throw new IllegalArgumentException(
                    "Email cannot be empty"
            );
        }

        return email
                .trim()
                .toLowerCase(Locale.ROOT);
    }
}