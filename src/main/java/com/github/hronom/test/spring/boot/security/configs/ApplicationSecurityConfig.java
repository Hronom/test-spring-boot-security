package com.github.hronom.test.spring.boot.security.configs;

import com.github.hronom.test.spring.boot.security.components.AuthenticatedUserManager;
import com.github.hronom.test.spring.boot.security.configs.custom.objects.CustomAuthenticationProvider;
import com.github.hronom.test.spring.boot.security.configs.custom.objects.RestAuthenticationEntryPoint;
import com.github.hronom.test.spring.boot.security.filters.CustomTokenAuthenticationFilter;
import com.github.hronom.test.spring.boot.security.filters.CustomUsernamePasswordAuthenticationFilter;
import com.github.hronom.test.spring.boot.security.handlers.CustomAccessDeniedHandler;
import com.github.hronom.test.spring.boot.security.handlers.CustomAuthenticationSuccessHandler;
import com.github.hronom.test.spring.boot.security.handlers.CustomLogoutSuccessHandler;
import com.github.hronom.test.spring.boot.security.handlers.CustomUrlAuthenticationFailureHandler;

import org.springframework.boot.autoconfigure.security.SecurityProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
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
            .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            .sessionFixation().none()
            .and()
            .httpBasic().authenticationEntryPoint(restAuthenticationEntryPoint())
            .and()
            .exceptionHandling().accessDeniedHandler(new CustomAccessDeniedHandler())
            .and()
            .formLogin().disable()
            .logout()
            .logoutUrl("/api/logout").permitAll()
            .logoutSuccessHandler(new CustomLogoutSuccessHandler(authenticatedUserManager()))
            .and()
            // Disable CSRF for making /logout available for all HTTP methods (POST, GET...)
            .csrf()
            .disable();

        http.addFilterBefore(
            customUsernamePasswordAuthenticationFilter(),
            UsernamePasswordAuthenticationFilter.class
        );
        http.addFilterAfter(
            customTokenAuthenticationFilter(),
            CustomUsernamePasswordAuthenticationFilter.class
        );
    }

    @Override
    public void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.authenticationProvider(new CustomAuthenticationProvider(authenticatedUserManager()));
    }

    @Bean
    public RestAuthenticationEntryPoint restAuthenticationEntryPoint() throws Exception {
        return new RestAuthenticationEntryPoint();
    }

    @Bean
    public AuthenticatedUserManager authenticatedUserManager() throws Exception {
        return new AuthenticatedUserManager(3000);
    }

    @Bean
    public CustomUsernamePasswordAuthenticationFilter customUsernamePasswordAuthenticationFilter() throws Exception {
        CustomUsernamePasswordAuthenticationFilter filter =
            new CustomUsernamePasswordAuthenticationFilter("/api/login");
        filter.setAuthenticationManager(authenticationManager());
        filter.setAuthenticationSuccessHandler(new CustomAuthenticationSuccessHandler(authenticatedUserManager()));
        filter.setAuthenticationFailureHandler(new CustomUrlAuthenticationFailureHandler());
        return filter;
    }

    @Bean
    public CustomTokenAuthenticationFilter customTokenAuthenticationFilter() throws Exception {
        CustomTokenAuthenticationFilter filter =
            new CustomTokenAuthenticationFilter("/api/**", authenticatedUserManager());
        filter.setAuthenticationManager(authenticationManager());
        filter.setAuthenticationSuccessHandler(new CustomAuthenticationSuccessHandler(authenticatedUserManager()));
        filter.setAuthenticationFailureHandler(new CustomUrlAuthenticationFailureHandler());
        return filter;
    }
}
