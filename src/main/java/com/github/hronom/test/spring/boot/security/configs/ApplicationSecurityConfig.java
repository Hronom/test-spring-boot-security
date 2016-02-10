package com.github.hronom.test.spring.boot.security.configs;

import com.github.hronom.test.spring.boot.security.configs.custom.objects.CustomAuthenticationProvider;
import com.github.hronom.test.spring.boot.security.filters.CustomUsernamePasswordAuthenticationFilter;
import com.github.hronom.test.spring.boot.security.configs.custom.objects.RestAuthenticationEntryPoint;
import com.github.hronom.test.spring.boot.security.handlers.CustomAuthenticationSuccessHandler;
import com.github.hronom.test.spring.boot.security.handlers.CustomUrlAuthenticationFailureHandler;

import org.springframework.boot.autoconfigure.security.SecurityProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableGlobalMethodSecurity(securedEnabled = true)
@Order(SecurityProperties.ACCESS_OVERRIDE_ORDER)
public class ApplicationSecurityConfig extends WebSecurityConfigurerAdapter {
    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
            .requiresChannel()
            .antMatchers("/api/login").requiresSecure()
            .antMatchers("/api/**").requiresInsecure()
            .and()
            .authorizeRequests()
            .antMatchers("/api/").permitAll()
            .antMatchers("/api/login").permitAll()
            .antMatchers("/api/roles").permitAll()
            .antMatchers("/api/**").fullyAuthenticated()
            .and()
            // Added the sessionFixation = "none" because If I only include
            // requiresChannel = "http" it doesn't go further from the login.
            // I try to log in but I come back to the login.
            // Original: http://stackoverflow.com/q/28341645/285571
            .sessionManagement()
            .sessionFixation().none()
            .and()
            .httpBasic().authenticationEntryPoint(new RestAuthenticationEntryPoint())
            .and()
            .formLogin().disable()
            .logout()
            .logoutUrl("/api/logout").permitAll()
            .logoutSuccessUrl("/api/").permitAll()
            .and()
            // Disable CSRF for making /logout available for all HTTP methods (POST, GET...)
            .csrf()
            .disable();

        http.addFilterBefore(customUsernamePasswordAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class);
    }

    @Override
    public void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.authenticationProvider(new CustomAuthenticationProvider());
    }

    @Bean
    public CustomUsernamePasswordAuthenticationFilter customUsernamePasswordAuthenticationFilter() throws Exception {
        CustomUsernamePasswordAuthenticationFilter filter =
            new CustomUsernamePasswordAuthenticationFilter("/api/login");
        filter.setAuthenticationManager(authenticationManager());
        filter.setAuthenticationSuccessHandler(new CustomAuthenticationSuccessHandler("/api/"));
        filter.setAuthenticationFailureHandler(new CustomUrlAuthenticationFailureHandler());
        return filter;
    }
}
