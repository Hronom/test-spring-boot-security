package com.github.hronom.test.spring.boot.security.controllers;

import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class SampleController {
    @RequestMapping("/")
    public String root() {
        return "welcome";
    }

    @RequestMapping("/admin")
    @Secured("ROLE_ADMIN")
    public String admin() {
        return "admin";
    }

    @RequestMapping("/user")
    @Secured("ROLE_USER")
    public String user() {
        return "user";
    }
}
