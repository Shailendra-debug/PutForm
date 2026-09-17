package com.skushwaha.getform.ClientApi.Config;

import com.skushwaha.getform.Auth.UserPrincipal;
import lombok.Getter;
import org.springframework.security.authentication.AbstractAuthenticationToken;

public class ApiKeyAuthenticationToken
        extends AbstractAuthenticationToken {

    @Getter
    private final Long apiKeyId;
    private final UserPrincipal principal;

    public ApiKeyAuthenticationToken(
            Long apiKeyId,
            UserPrincipal principal
    ) {
        super(principal.getAuthorities());

        this.apiKeyId = apiKeyId;
        this.principal = principal;

        setAuthenticated(true);
    }

    @Override
    public Object getPrincipal() {
        return principal;
    }

    @Override
    public Object getCredentials() {
        return null;
    }
}