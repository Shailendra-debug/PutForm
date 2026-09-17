package com.skushwaha.getform.ClientApi.DTO;


public record RateLimitResult(
        boolean allowed,
        long limit,
        long remaining,
        long retryAfterSeconds
) {
}