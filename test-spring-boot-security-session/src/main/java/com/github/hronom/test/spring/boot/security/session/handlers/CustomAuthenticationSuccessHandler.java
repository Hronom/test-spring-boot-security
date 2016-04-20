package com.github.hronom.test.spring.boot.security.session.handlers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

import java.io.IOException;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class CustomAuthenticationSuccessHandler implements AuthenticationSuccessHandler {
    private final SessionRegistry sessionRegistry;

    @Autowired
    public CustomAuthenticationSuccessHandler(SessionRegistry sessionRegistryArg) {
        sessionRegistry = sessionRegistryArg;
    }


    @Override
    public void onAuthenticationSuccess(
        HttpServletRequest request,
        HttpServletResponse response,
        Authentication authentication
    ) throws IOException, ServletException {
        response.setStatus(HttpServletResponse.SC_OK);
        // user object = User currently updated
        // invalidate user session
        List<Object> loggedUsers = sessionRegistry.getAllPrincipals();
        for (Object principal : loggedUsers) {
            /*if(principal instanceof User) {
                final User loggedUser = (User) principal;
                if(user.getUsername().equals(loggedUser.getUsername())) {
                    List<SessionInformation> sessionsInfo = sessionRegistry.getAllSessions(principal, false);
                    if(null != sessionsInfo && sessionsInfo.size() > 0) {
                        for (SessionInformation sessionInformation : sessionsInfo) {
                            LOGGER.info("Exprire now :" + sessionInformation.getSessionId());
                            sessionInformation.expireNow();
                            sessionRegistry.removeSessionInformation(sessionInformation.getSessionId());
                            // User is not forced to re-logging
                        }
                    }
                }
            }*/
        }
    }
}