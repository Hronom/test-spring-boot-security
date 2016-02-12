package com.github.hronom.test.spring.boot.security.controllers;

import com.github.hronom.test.spring.boot.security.SampleWebSecureApplication;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.embedded.EmbeddedWebApplicationContext;
import org.springframework.boot.test.IntegrationTest;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.security.web.FilterChainProxy;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

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
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
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
                .perform(post("/login").param("username", "admin").param("password", "admin").secure(true))
                .andReturn()
                .getRequest()
                .getSession();
        MvcResult result = mockMvc.perform(get("/roles").session((MockHttpSession)session)).andReturn();
        System.out.println(result.getResponse().getContentAsString());
    }

    @Test
    public void testNotLoggedAdmin() throws Exception {
        mockMvc
            .perform(get("/admin"))
            .andExpect(content().string(""))
            .andExpect(status().isUnauthorized());
    }
}