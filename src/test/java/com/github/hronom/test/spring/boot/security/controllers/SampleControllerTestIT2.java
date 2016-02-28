package com.github.hronom.test.spring.boot.security.controllers;

import com.github.hronom.test.spring.boot.security.SampleWebSecureApplication;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.web.FilterChainProxy;
import org.springframework.security.web.method.annotation.AuthenticationPrincipalArgumentResolver;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = SampleWebSecureApplication.class)
@WebAppConfiguration
public class SampleControllerTestIT2 {
    private MockMvc mockMvc;

    @Autowired
    private FilterChainProxy springSecurityFilterChain;

    @Before
    public void setup() {
        mockMvc = MockMvcBuilders
            .standaloneSetup(new SampleController())
            .setCustomArgumentResolvers(new AuthenticationPrincipalArgumentResolver())
            .addFilters(springSecurityFilterChain).build();
    }

    @Test
    public void testNotLoggedUser2() throws Exception {
//        ArrayList<SimpleGrantedAuthority> authorities = new ArrayList<>();
//        authorities.add(new SimpleGrantedAuthority("ROLE_ADMIN"));
//        authorities.add(new SimpleGrantedAuthority("ROLE_USER"));
//        CustomUser user = new CustomUser("admin", authorities);

        mockMvc
            .perform(get("/adminX")/*.principal(user)*/)
            .andExpect(content().string("adminX"))
            .andExpect(status().isOk());
    }
}