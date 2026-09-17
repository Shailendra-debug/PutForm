package com.skushwaha.getform.Auth.Service;

import com.skushwaha.getform.Auth.DTO.PendingRegistration;
import com.skushwaha.getform.Exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import tools.jackson.databind.ObjectMapper;

import java.time.Duration;

@Service
@RequiredArgsConstructor
public class RegistrationRedisService {

    private final StringRedisTemplate redisTemplate;
    private final ObjectMapper objectMapper;

    private static final String REGISTRATION_PREFIX =
            "registration:";

    private static final String OTP_PREFIX =
            "registration:otp:";

    private static final Duration REGISTRATION_EXPIRATION =
            Duration.ofMinutes(10);

    private static final Duration OTP_EXPIRATION =
            Duration.ofMinutes(5);


    // ==========================================
    // SAVE REGISTRATION
    // ==========================================
    public void saveRegistration(
            String email,
            PendingRegistration registration
    ) {

        String key = REGISTRATION_PREFIX + email;

        try {

            String json =
                    objectMapper.writeValueAsString(
                            registration
                    );

            redisTemplate.opsForValue().set(
                    key,
                    json,
                    REGISTRATION_EXPIRATION
            );

        } catch (Exception e) {

            throw new ResourceNotFoundException(
                    "Failed to save pending registration"
            );
        }
    }


    // ==========================================
    // GET PENDING REGISTRATION
    // ==========================================

    public PendingRegistration getRegistration(
            String email
    ) {

        String key = REGISTRATION_PREFIX + email;

        String json =
                redisTemplate
                        .opsForValue()
                        .get(key);

        if (json == null) {
            return null;
        }

        try {

            return objectMapper.readValue(
                    json,
                    PendingRegistration.class
            );

        } catch (Exception e) {

            throw new ResourceNotFoundException(
                    "Failed to read pending registration"
            );
        }
    }


    // ==========================================
    // DELETE PENDING REGISTRATION
    // ==========================================

    public void deleteRegistration(
            String email
    ) {

        redisTemplate.delete(
                REGISTRATION_PREFIX + email
        );
    }
}