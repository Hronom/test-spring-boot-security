package com.github.hronom.test.spring.boot.security.session.controllers;

import com.github.hronom.test.spring.boot.security.session.configs.custom.objects.CustomUser;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.core.session.SessionInformation;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.security.web.authentication.logout.CookieClearingLogoutHandler;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.security.web.authentication.rememberme.AbstractRememberMeServices;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/admin")
@Secured("ROLE_ADMIN")
public class AdminController {
    private static final Logger logger = LogManager.getLogger();

    private final SessionRegistry sessionRegistry;

    @Autowired
    public AdminController(SessionRegistry sessionRegistryArg) {
        sessionRegistry = sessionRegistryArg;
    }

    @RequestMapping("/invalidate/user/{username}")
    public String invalidateUser(
        @PathVariable("username") String username
    ) {
        // From http://forum.spring.io/forum/spring-projects/security/115642-invalidate-user-session-and-force-re-logging
        // Invalidate user session.
        List<Object> loggedUsers = sessionRegistry.getAllPrincipals();
        for (Object principal : loggedUsers) {
            System.out.println(principal.toString());
            if(principal instanceof CustomUser) {
                 CustomUser loggedUser = (CustomUser) principal;
                if (loggedUser.getName().equals(username)) {
                    List<SessionInformation> sessionsInfo =
                        sessionRegistry.getAllSessions(principal, false);
                    if (sessionsInfo != null && sessionsInfo.size() > 0) {
                        for (SessionInformation sessionInformation : sessionsInfo) {
                            logger.info("Expire now: " + sessionInformation.getSessionId());
                            sessionInformation.expireNow();
                            //sessionRegistry.removeSessionInformation(sessionInformation.getSessionId());
                            // User is not forced to re-logging
                        }
                    }
                }
            }
        }
        return "";
    }
}
