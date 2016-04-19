package com.github.hronom.test.spring.boot.security.token.controllers;

import com.github.hronom.test.spring.boot.security.token.SampleWebSecureApplication;
import com.github.hronom.test.spring.boot.security.token.configs.custom.objects.CustomUser;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.embedded.EmbeddedWebApplicationContext;
import org.springframework.boot.test.IntegrationTest;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.web.FilterChainProxy;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.ArrayList;

import javax.servlet.http.HttpSession;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

// More info about annotations:
// http://www.jayway.com/2014/07/04/integration-testing-a-spring-boot-application/
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = SampleWebSecureApplication.class)
@WebAppConfiguration
@IntegrationTest
// SpringJUnit4ClassRunner does not close the Application Context at the end of JUnit test case
// http://stackoverflow.com/questions/7498202/springjunit4classrunner-does-not-close-the-application-context-at-the-end-of-jun
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class SampleControllerTestIT {
    @Autowired
    private FilterChainProxy filterChain;

    @Autowired
    private EmbeddedWebApplicationContext server;

    private MockMvc mockMvc;

    @Before
    public void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(server).addFilter(filterChain).build();
    }

    @Test
    public void testWelcome() throws Exception {
        mockMvc
            .perform(get("/"))
            .andExpect(content().string("welcome"))
            .andExpect(status().isOk());
    }

    @Test
    public void testLogin() throws Exception {
        HttpSession session =
            mockMvc
                .perform(post("/api/login").param("username", "admin").param("password", "admin").secure(true))
                .andReturn()
                .getRequest()
                .getSession();
        MvcResult result = mockMvc.perform(get("/api/roles").session((MockHttpSession)session)).andReturn();
        System.out.println(result.getResponse().getContentAsString());
    }

    @Test
    public void testLoggedAdmin() throws Exception {
        HttpSession session =
            mockMvc
                .perform(post("/api/login").param("username", "admin").param("password", "admin").secure(true))
                .andReturn()
                .getRequest()
                .getSession();
        mockMvc
            .perform(get("/admin").session((MockHttpSession)session))
            .andExpect(content().string("admin username \"admin\"")).andExpect(status().isOk());
    }

    @Test
    public void testNotLoggedAdmin() throws Exception {
        mockMvc
            .perform(get("/api/admin"))
            .andExpect(content().string(""))
            .andExpect(status().isUnauthorized());
    }

    @Test
    public void testNotLoggedUser() throws Exception {
        mockMvc
            .perform(get("/user"))
            .andExpect(content().string(""))
            .andExpect(status().isUnauthorized());
    }

    @Test
    public void testNotLoggedUser2() throws Exception {
        ArrayList<SimpleGrantedAuthority> authorities = new ArrayList<>();
        authorities.add(new SimpleGrantedAuthority("ROLE_ADMIN"));
        authorities.add(new SimpleGrantedAuthority("ROLE_USER"));
        CustomUser user = new CustomUser("admin", authorities);
        
        mockMvc
            .perform(get("/api/user").principal(user))
            .andExpect(content().string(""))
            .andExpect(status().isUnauthorized());
    }
}