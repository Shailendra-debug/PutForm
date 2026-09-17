package com.skushwaha.getform.RateLimit.Filter;

import com.skushwaha.getform.ClientApi.Config.ApiKeyAuthenticationToken;
import com.skushwaha.getform.ClientApi.DTO.RateLimitResult;
import com.skushwaha.getform.RateLimit.Service.RateLimitService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class RateLimitFilter extends OncePerRequestFilter {

    private final RateLimitService rateLimitService;

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {

        Authentication authentication =
                SecurityContextHolder
                        .getContext()
                        .getAuthentication();


        if (!(authentication
                instanceof ApiKeyAuthenticationToken apiKeyAuth)) {

            filterChain.doFilter(request, response);
            return;
        }

        Long apiKeyId = apiKeyAuth.getApiKeyId();

        RateLimitResult result;

        try {

            result = rateLimitService.check(apiKeyId);

        } catch (Exception e) {

            /*
             * Redis/rate-limit service failed.
             *
             * Fail closed because this is a protected API.
             */
            response.setStatus(
                    HttpServletResponse.SC_SERVICE_UNAVAILABLE
            );

            response.setContentType("application/json");

            response.getWriter().write("""
                {
                    "success": false,
                    "message": "Rate limiting service unavailable"
                }
                """);

            return;
        }

        /*
         * Rate-limit headers.
         */
        response.setHeader(
                "X-RateLimit-Limit",
                String.valueOf(result.limit())
        );

        response.setHeader(
                "X-RateLimit-Remaining",
                String.valueOf(result.remaining())
        );

        /*
         * Rate limit exceeded.
         */
        if (!result.allowed()) {

            response.setStatus(429);

            response.setContentType("application/json");

            response.setHeader(
                    "Retry-After",
                    String.valueOf(
                            Math.max(
                                    1,
                                    result.retryAfterSeconds()
                            )
                    )
            );

            response.getWriter().write("""
                {
                    "success": false,
                    "message": "Rate limit exceeded. Try again later."
                }
                """);

            return;
        }

        /*
         * Request is allowed.
         */
        filterChain.doFilter(request, response);
    }
}