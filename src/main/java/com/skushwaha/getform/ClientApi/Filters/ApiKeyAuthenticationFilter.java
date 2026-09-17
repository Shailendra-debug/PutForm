package com.skushwaha.getform.ClientApi.Filters;

import com.skushwaha.getform.Auth.UserPrincipal;
import com.skushwaha.getform.ClientApi.Config.ApiKeyAuthenticationToken;
import com.skushwaha.getform.ClientApi.Config.ApiKeyHasher;
import com.skushwaha.getform.ClientApi.Entity.ApiKey;
import com.skushwaha.getform.ClientApi.Repository.ApiKeyRepository;
import com.skushwaha.getform.ClientApi.Service.EncryptionService;
import com.skushwaha.getform.Users.Entity.User;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class ApiKeyAuthenticationFilter
        extends OncePerRequestFilter {

    private static final String API_KEY_HEADER = "X-API-KEY";

    private final EncryptionService encryptionService;
    private final ApiKeyRepository apiKeyRepository;

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {

        String apiKey =
                request.getHeader(API_KEY_HEADER);

        /*
         * No API key → continue normally.
         *
         * This allows JWT authentication to work.
         */
        if (apiKey == null || apiKey.isBlank()) {
            filterChain.doFilter(request, response);
            return;
        }

        /*
         * Do not allow two authentication mechanisms
         * at the same time.
         *
         * Example:
         * JWT Cookie + X-API-KEY
         */
        if (SecurityContextHolder
                .getContext()
                .getAuthentication() != null) {

            response.setStatus(
                    HttpServletResponse.SC_BAD_REQUEST
            );

            response.setContentType("application/json");

            response.getWriter().write("""
                {
                    "success": false,
                    "message": "Multiple authentication methods are not allowed"
                }
                """);

            return;
        }

        /*
         * Hash the raw API key.
         *
         * The raw API key is NEVER stored in the database.
         */
        String hash =
                encryptionService.encrypt(apiKey);

        /*
         * Find active API key + user.
         *
         * JOIN FETCH prevents LazyInitializationException.
         */
        Optional<ApiKey> optional =
                apiKeyRepository
                        .findActiveByKeyHashWithUser(hash);

        /*
         * API key doesn't exist.
         */
        if (optional.isEmpty()) {

            response.setStatus(
                    HttpServletResponse.SC_UNAUTHORIZED
            );

            response.setContentType("application/json");

            response.getWriter().write("""
                {
                    "success": false,
                    "message": "Invalid API key"
                }
                """);

            return;
        }

        ApiKey storedKey = optional.get();

        /*
         * Check expiration.
         *
         * expiresAt == now is considered expired.
         */
        LocalDateTime now = LocalDateTime.now();

        if (storedKey.getExpiresAt() != null &&
                !storedKey.getExpiresAt().isAfter(now)) {

            response.setStatus(
                    HttpServletResponse.SC_UNAUTHORIZED
            );

            response.setContentType("application/json");

            response.getWriter().write("""
                {
                    "success": false,
                    "message": "API key expired"
                }
                """);

            return;
        }

        /*
         * Create Spring Security authentication.
         */
        ApiKeyAuthenticationToken authentication =
                createAuthentication(storedKey);

        authentication.setDetails(
                new WebAuthenticationDetailsSource()
                        .buildDetails(request)
        );

        /*
         * Store authentication in SecurityContext.
         */
        SecurityContextHolder
                .getContext()
                .setAuthentication(authentication);

        /*
         * Continue request.
         */
        filterChain.doFilter(request, response);
    }

    private ApiKeyAuthenticationToken createAuthentication(
            ApiKey storedKey
    ) {

        User user = storedKey.getUser();

        UserPrincipal principal =
                new UserPrincipal(
                        user.getId(),
                        user.getEmail(),
                        List.of(
                                new SimpleGrantedAuthority(
                                        "ROLE_" + user.getRole().name()
                                )
                        )
                );

        return new ApiKeyAuthenticationToken(
                storedKey.getId(),
                principal
        );
    }
}