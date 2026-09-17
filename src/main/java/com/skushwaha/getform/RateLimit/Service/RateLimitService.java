package com.skushwaha.getform.RateLimit.Service;

import com.skushwaha.getform.ClientApi.DTO.RateLimitResult;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.List;

@Service
@RequiredArgsConstructor
public class RateLimitService {

    private final StringRedisTemplate redisTemplate;

    private static final int CAPACITY = 3;

    /*
     * 100 requests per 60 seconds.
     */
    private static final double REFILL_RATE =
            5.0 / 60.0;

    private static final String SCRIPT = """
        local key = KEYS[1]

        local capacity = tonumber(ARGV[1])
        local refillRate = tonumber(ARGV[2])
        local now = tonumber(ARGV[3])
        local ttl = tonumber(ARGV[4])

        local data = redis.call(
            'HMGET',
            key,
            'tokens',
            'timestamp'
        )

        local tokens = tonumber(data[1])
        local timestamp = tonumber(data[2])

        if tokens == nil then
            tokens = capacity
            timestamp = now
        end

        local elapsed = math.max(0, now - timestamp)

        local refill =
            (elapsed / 1000.0) * refillRate

        tokens = math.min(
            capacity,
            tokens + refill
        )

        local allowed = 0
        local retryAfter = 0

        if tokens >= 1 then

            tokens = tokens - 1
            allowed = 1

        else

            local missing = 1 - tokens

            retryAfter =
                math.ceil(
                    (missing / refillRate) * 1000
                )

        end

        redis.call(
            'HSET',
            key,
            'tokens',
            tokens,
            'timestamp',
            now
        )

        redis.call(
            'PEXPIRE',
            key,
            ttl
        )

        local remaining =
            math.floor(tokens)

        return {
            allowed,
            remaining,
            retryAfter
        }
        """;

    private final DefaultRedisScript<List> rateLimitScript =
            new DefaultRedisScript<>(
                    SCRIPT,
                    List.class
            );

    public RateLimitResult check(Long apiKeyId) {

        String key =
                "rate-limit:api-key:" + apiKeyId;

        long now =
                System.currentTimeMillis();

        long ttl =
                120_000L;

        List<?> result =
                redisTemplate.execute(
                        rateLimitScript,
                        List.of(key),
                        String.valueOf(CAPACITY),
                        String.valueOf(REFILL_RATE),
                        String.valueOf(now),
                        String.valueOf(ttl)
                );

        if (result == null || result.size() < 3) {
            throw new IllegalStateException(
                    "Invalid Redis rate-limit response"
            );
        }
        long allowed =
                ((Number) result.get(0)).longValue();

        long remaining =
                ((Number) result.get(1)).longValue();

        long retryAfterMillis =
                ((Number) result.get(2)).longValue();

        long retryAfterSeconds =
                (long) Math.ceil(
                        retryAfterMillis / 1000.0
                );

        return new RateLimitResult(
                allowed == 1,
                CAPACITY,
                remaining,
                retryAfterSeconds
        );
    }
}