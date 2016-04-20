package com.github.hronom.test.spring.boot.security.session.handlers;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class CustomUrlAuthenticationFailureHandler implements AuthenticationFailureHandler {
    private static final Logger logger = LogManager.getLogger();

    @Override
    public void onAuthenticationFailure(
        HttpServletRequest request, HttpServletResponse response, AuthenticationException exception
    ) throws IOException, ServletException {
        logger.debug("No failure URL set, sending 401 Unauthorized error");
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
    }
}