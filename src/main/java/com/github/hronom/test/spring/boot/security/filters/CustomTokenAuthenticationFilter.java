package com.github.hronom.test.spring.boot.security.filters;

import com.github.hronom.test.spring.boot.security.components.AuthenticatedUserManager;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;
import org.springframework.security.web.authentication.preauth.PreAuthenticatedCredentialsNotFoundException;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class CustomTokenAuthenticationFilter
    extends AbstractAuthenticationProcessingFilter {

    private final String tokenParameter = "Token";

    private final AuthenticatedUserManager authenticatedUserManager;

    public CustomTokenAuthenticationFilter(
        String url,
        AuthenticatedUserManager authenticatedUserManagerArg
    ) {
        super(new AntPathRequestMatcher(url));
        authenticatedUserManager = authenticatedUserManagerArg;
    }

    @Override
    public Authentication attemptAuthentication(
        HttpServletRequest request, HttpServletResponse response
    ) throws AuthenticationException {
        String tokenId = obtainToken(request);
        Authentication authenticationToken = authenticatedUserManager.getAuthentication(tokenId);
        if (authenticationToken == null) {
            throw new PreAuthenticatedCredentialsNotFoundException("Fail");
        }
        return authenticationToken;
    }

    protected String obtainToken(HttpServletRequest request) {
        return request.getHeader(tokenParameter);
    }
}