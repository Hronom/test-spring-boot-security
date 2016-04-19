package com.github.hronom.test.spring.boot.security.token.handlers;

import com.github.hronom.test.spring.boot.security.token.components.AuthenticatedUserManager;

import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class CustomAuthenticationSuccessHandler implements AuthenticationSuccessHandler {
    private final AuthenticatedUserManager authenticatedUserManager;

    public CustomAuthenticationSuccessHandler(AuthenticatedUserManager authenticatedUserManagerArg) {
        authenticatedUserManager = authenticatedUserManagerArg;
    }

    @Override
    public void onAuthenticationSuccess(
        HttpServletRequest request,
        HttpServletResponse response,
        Authentication authentication
    ) throws IOException, ServletException {
        String tokenId = authenticatedUserManager.getToken(authentication);
        response.setStatus(HttpServletResponse.SC_OK);
        response.addHeader("Token", tokenId);
    }
}