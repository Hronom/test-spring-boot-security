package com.github.hronom.test.spring.boot.security.handlers;

import com.github.hronom.test.spring.boot.security.components.AuthenticatedUserManager;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class CustomLogoutSuccessHandler implements LogoutSuccessHandler {
    private final AuthenticatedUserManager authenticatedUserManager;

    public CustomLogoutSuccessHandler(AuthenticatedUserManager authenticatedUserManagerArg) {
        authenticatedUserManager = authenticatedUserManagerArg;
    }

    @Override
    public void onLogoutSuccess(
        HttpServletRequest request, HttpServletResponse response, Authentication authentication
    ) throws IOException, ServletException {
        authenticatedUserManager.freeToken((UsernamePasswordAuthenticationToken)authentication);
        response.setStatus(HttpServletResponse.SC_OK);
    }
}
