package com.skushwaha.getform.Forms.Service;


import com.skushwaha.getform.Forms.DTO.FormResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import tools.jackson.databind.ObjectMapper;

import java.time.Duration;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PublicFormRedisService {

    private final StringRedisTemplate redisTemplate;

    private final ObjectMapper objectMapper;

    private static final String PREFIX =
            "getform:v1:public-form:";

    private static final Duration EXPIRY =
            Duration.ofMinutes(5);


    // ==========================================
    // SAVE
    // ==========================================

    public void save(
            UUID formId,
            FormResponse response
    ) {

        String key =
                PREFIX + formId;

        try {

            String json =
                    objectMapper.writeValueAsString(
                            response
                    );

            redisTemplate.opsForValue().set(
                    key,
                    json,
                    EXPIRY
            );

        } catch (Exception e) {

            throw new IllegalStateException(
                    "Failed to cache public form",
                    e
            );
        }
    }


    // ==========================================
    // GET
    // ==========================================

    public FormResponse get(
            UUID formId
    ) {

        String key =
                PREFIX + formId;

        String json =
                redisTemplate.opsForValue()
                        .get(key);

        if (json == null) {
            return null;
        }

        try {

            return objectMapper.readValue(
                    json,
                    FormResponse.class
            );

        } catch (Exception e) {

            // Remove corrupted cache
            redisTemplate.delete(key);

            throw new IllegalStateException(
                    "Failed to read cached public form",
                    e
            );
        }
    }


    // ==========================================
    // DELETE CACHE
    // ==========================================

    public void delete(
            UUID formId
    ) {

        redisTemplate.delete(
                PREFIX + formId
        );
    }
}