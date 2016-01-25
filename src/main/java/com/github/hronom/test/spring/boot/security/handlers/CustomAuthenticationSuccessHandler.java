package com.github.hronom.test.spring.boot.security.handlers;

import org.springframework.security.web.RedirectStrategy;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;

import java.io.IOException;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class CustomAuthenticationSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {
    @PostConstruct
    public void afterPropertiesSet() {
        setRedirectStrategy(new NoRedirectStrategy());
    }

    protected class NoRedirectStrategy implements RedirectStrategy {
        @Override
        public void sendRedirect(
            HttpServletRequest request, HttpServletResponse response, String url
        ) throws IOException {
            // No redirect.
        }
    }
}