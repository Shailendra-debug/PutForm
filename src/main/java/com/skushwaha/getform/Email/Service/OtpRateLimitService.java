package com.skushwaha.getform.Email.Service;

import com.skushwaha.getform.Exception.OtpRateLimitException;
import com.skushwaha.getform.Exception.OtpResendTooSoonException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class OtpRateLimitService {

    private final StringRedisTemplate redisTemplate;

    private static final int MAX_OTP_REQUESTS = 5;

    private static final Duration RATE_LIMIT_WINDOW =
            Duration.ofMinutes(5);

    private static final Duration RESEND_COOLDOWN =
            Duration.ofSeconds(30);


    private String rateLimitKey(String email) {
        return "otp:rate-limit:" +
                email.toLowerCase().trim();
    }


    private String resendKey(String email) {
        return "otp:resend:" +
                email.toLowerCase().trim();
    }


    public void checkAndRecord(String email) {

        email = email.toLowerCase().trim();

        String rateKey = rateLimitKey(email);
        String resendKey = resendKey(email);


        // ==========================================
        // 1. CHECK 30 SECOND RESEND COOLDOWN
        // ==========================================

        Boolean resendExists =
                redisTemplate.hasKey(resendKey);

        if (Boolean.TRUE.equals(resendExists)) {

            Long remaining =
                    redisTemplate.getExpire(
                            resendKey,
                            TimeUnit.SECONDS
                    );

            throw new OtpResendTooSoonException(
                    "Please wait " +
                            remaining +
                            " seconds before requesting another OTP."
            );
        }


        // ==========================================
        // 2. CHECK 5 OTP / 5 MINUTES
        // ==========================================

        Long count =
                redisTemplate.opsForValue()
                        .increment(rateKey);


        if (count == null) {
            throw new IllegalStateException(
                    "Unable to process OTP request"
            );
        }


        // First request → start 5-minute window
        if (count == 1) {

            redisTemplate.expire(
                    rateKey,
                    RATE_LIMIT_WINDOW
            );
        }


        // ==========================================
        // 3. MAX 5 OTP
        // ==========================================

        if (count > MAX_OTP_REQUESTS) {

            throw new OtpRateLimitException(
                    "Too many OTP requests. " +
                            "Please try again later."
            );
        }


        // ==========================================
        // 4. START 30 SECOND COOLDOWN
        // ==========================================

        redisTemplate.opsForValue()
                .set(
                        resendKey,
                        "1",
                        RESEND_COOLDOWN
                );
    }
}
