package com.github.hronom.test.spring.boot.security.token.controllers;

import org.springframework.security.access.annotation.Secured;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;
import java.util.Collection;

@RestController
public class SampleController {
    @RequestMapping("/")
    public String root() {
        return "welcome";
    }

    @RequestMapping("/admin")
    @Secured("ROLE_ADMIN")
    public String admin(Principal principal) {
        //User user = (User) principal;
        return "admin" + " username \"" + principal.getName() + "\"";
    }

    @RequestMapping("/user")
    @Secured("ROLE_USER")
    public String user(Principal principal) {
        //User user = (User) principal;
        return "user" + " username \"" + principal.getName() + "\"";
    }

    @RequestMapping("/roles")
    public String roles() {
        Collection<SimpleGrantedAuthority> authorities =
            (Collection<SimpleGrantedAuthority>) SecurityContextHolder
            .getContext()
            .getAuthentication()
            .getAuthorities();
        return authorities.toString();
    }
}
