package com.github.hronom.test.spring.boot.security.session.handlers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.security.authentication.event.AbstractAuthenticationEvent;
import org.springframework.security.authentication.event.AuthenticationSuccessEvent;
import org.springframework.security.authentication.event.InteractiveAuthenticationSuccessEvent;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.session.SessionInformation;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.security.web.authentication.WebAuthenticationDetails;
import org.springframework.security.web.authentication.logout.LogoutHandler;

import java.util.Collections;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class AccessLogHandler implements ApplicationListener<AbstractAuthenticationEvent>,
    LogoutHandler {

    @Autowired
    private SessionRegistry sessionRegistry;

    public void onApplicationEvent(AbstractAuthenticationEvent event) {
        if (event instanceof AuthenticationSuccessEvent || event instanceof InteractiveAuthenticationSuccessEvent) {
            Authentication authentication = event.getAuthentication();
            WebAuthenticationDetails details = (WebAuthenticationDetails) authentication.getDetails();
            System.out.println(
                "IP: " + details.getRemoteAddress() + " " + authentication.getName() + " " +
                details.getSessionId());
        }
    }

    public void logout(HttpServletRequest request, HttpServletResponse response, Authentication authentication) {
        WebAuthenticationDetails details = (WebAuthenticationDetails) authentication.getDetails();
        System.out.println(Collections.singletonList(details.getSessionId()));
        SessionInformation si = sessionRegistry.getSessionInformation(details.getSessionId());
        if (si != null) {
            si.expireNow();
        }
    }
}