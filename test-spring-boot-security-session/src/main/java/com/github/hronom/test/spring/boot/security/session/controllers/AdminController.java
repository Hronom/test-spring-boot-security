package com.github.hronom.test.spring.boot.security.session.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.session.SessionInformation;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collection;
import java.util.List;

@RestController
@RequestMapping("/api/admin")
public class AdminController {
    private final SessionRegistry sessionRegistry;

    @Autowired
    public AdminController(SessionRegistry sessionRegistryArg) {
        sessionRegistry = sessionRegistryArg;
    }

    @RequestMapping("/invalidate/user/{username}")
    public String invalidateUser(
        @PathVariable("username") String username
    ) {
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
        return "";
    }
}
