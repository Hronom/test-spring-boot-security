package com.github.hronom.test.spring.boot.security.configs;

import com.github.hronom.test.spring.boot.security.configs.custom.objects.CustomAuthenticationProvider;
import com.github.hronom.test.spring.boot.security.handlers.CustomAuthenticationSuccessHandler;

import org.springframework.boot.autoconfigure.security.SecurityProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

@Configuration
@EnableGlobalMethodSecurity(securedEnabled = true)
@Order(SecurityProperties.ACCESS_OVERRIDE_ORDER)
public class ApplicationSecurityConfig extends WebSecurityConfigurerAdapter {
    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
            .requiresChannel()
            .antMatchers("/login").requiresSecure()
            .antMatchers("/**").requiresInsecure()
            .and()
            .authorizeRequests()
            .antMatchers("/").permitAll()
            .antMatchers("/roles").permitAll()
            .antMatchers("/**").fullyAuthenticated()
            .and()
            // Added the sessionFixation = "none" because If I only include
            // requiresChannel = "http" it doesn't go further from the login.
            // I try to log in but I come back to the login.
            // Original: http://stackoverflow.com/q/28341645/285571
            .sessionManagement()
            .sessionFixation().none()
            .and()
            .formLogin()
            .loginPage("/login").permitAll()
            .failureUrl("/loginError").permitAll()
            .successHandler(new CustomAuthenticationSuccessHandler())
            .usernameParameter("username")
            .passwordParameter("password")
            .and()
            .logout()
            .logoutUrl("/logout").permitAll()
            .logoutSuccessUrl("/").permitAll()
            .and()
            // Disable CSRF for making /logout available for all HTTP methods (POST, GET...)
            .csrf()
            .disable();
    }

    @Override
    public void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.authenticationProvider(new CustomAuthenticationProvider());
    }
}
