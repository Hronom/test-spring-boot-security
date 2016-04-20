package com.github.hronom.test.spring.boot.security.session.configs;

import com.github.hronom.test.spring.boot.security.session.configs.custom.objects.CustomAuthenticationProvider;
import com.github.hronom.test.spring.boot.security.session.configs.custom.objects.RestAuthenticationEntryPoint;
import com.github.hronom.test.spring.boot.security.session.filters.CustomUsernamePasswordAuthenticationFilter;
import com.github.hronom.test.spring.boot.security.session.handlers.CustomAccessDeniedHandler;
import com.github.hronom.test.spring.boot.security.session.handlers.CustomAuthenticationSuccessHandler;
import com.github.hronom.test.spring.boot.security.session.handlers.CustomLogoutSuccessHandler;
import com.github.hronom.test.spring.boot.security.session.handlers.CustomUrlAuthenticationFailureHandler;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.boot.autoconfigure.security.SecurityProperties;
import org.springframework.boot.context.embedded.ServletListenerRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.session.SessionRegistryImpl;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.session.CompositeSessionAuthenticationStrategy;
import org.springframework.security.web.authentication.session.ConcurrentSessionControlAuthenticationStrategy;
import org.springframework.security.web.authentication.session.RegisterSessionAuthenticationStrategy;
import org.springframework.security.web.authentication.session.SessionAuthenticationStrategy;
import org.springframework.security.web.authentication.session.SessionFixationProtectionStrategy;
import org.springframework.security.web.session.HttpSessionEventPublisher;

import java.util.Arrays;

@Configuration
@EnableGlobalMethodSecurity(securedEnabled = true)
@Order(SecurityProperties.ACCESS_OVERRIDE_ORDER)
public class ApplicationSecurityConfig extends WebSecurityConfigurerAdapter {
    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
            // Added the sessionFixation = "none" because If I only include requiresChannel = "http"
            // it doesn't go further from the login. I try to log in but I come back to the login.
            // Original: http://stackoverflow.com/q/28341645/285571
            .sessionManagement()
            .sessionAuthenticationStrategy(sessionAuthenticationStrategy(sessionRegistry()))
            .maximumSessions(1)
            .sessionRegistry(sessionRegistry());

        http
            .requiresChannel()
            .antMatchers("/api/**").requiresSecure();

        http
            .authorizeRequests()
            .antMatchers("/api/").permitAll()
            .antMatchers("/api/login").permitAll()
            .antMatchers("/api/roles").permitAll()
            .antMatchers("/api/**").fullyAuthenticated();

        http
            .httpBasic()
            .authenticationEntryPoint(restAuthenticationEntryPoint());

        http
            .exceptionHandling()
            .accessDeniedHandler(new CustomAccessDeniedHandler());

        http
            .formLogin().disable()
            .logout()
            .logoutUrl("/api/logout").permitAll()
            .logoutSuccessHandler(new CustomLogoutSuccessHandler());

        http
            // Disable CSRF for making /logout available for all HTTP methods (POST, GET...)
            .csrf().disable();

        http.addFilterBefore(
            customUsernamePasswordAuthenticationFilter(sessionRegistry()),
            UsernamePasswordAuthenticationFilter.class
        );
    }

    @Override
    public void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.authenticationProvider(new CustomAuthenticationProvider());
    }

    @Bean
    public static SessionAuthenticationStrategy sessionAuthenticationStrategy(SessionRegistryImpl sessionRegistry) {
        SessionAuthenticationStrategy
            sessionAuthenticationStrategy
            = new CompositeSessionAuthenticationStrategy(Arrays.asList(
            new ConcurrentSessionControlAuthenticationStrategy(sessionRegistry),
            new SessionFixationProtectionStrategy(),
            new RegisterSessionAuthenticationStrategy(sessionRegistry)
        ));
        return sessionAuthenticationStrategy;
    }

    /**
     * Until https://jira.spring.io/browse/SEC-2855
     * is closed, we need to have this custom sessionRegistry
     */
    @Bean
    @Scope(ConfigurableBeanFactory.SCOPE_SINGLETON)
    public static SessionRegistryImpl sessionRegistry() {
        SessionRegistryImpl sessionRegistryImpl = new SessionRegistryImpl();
        return sessionRegistryImpl;
    }

    @Bean
    public ServletListenerRegistrationBean<HttpSessionEventPublisher> httpSessionEventPublisher() {
        return new ServletListenerRegistrationBean<>(new HttpSessionEventPublisher());
    }

    @Bean
    public RestAuthenticationEntryPoint restAuthenticationEntryPoint() throws Exception {
        return new RestAuthenticationEntryPoint();
    }

    @Bean
    public CustomUsernamePasswordAuthenticationFilter customUsernamePasswordAuthenticationFilter(SessionRegistryImpl sessionRegistry) throws Exception {
        CustomUsernamePasswordAuthenticationFilter filter =
            new CustomUsernamePasswordAuthenticationFilter("/api/login");
        filter.setAuthenticationManager(authenticationManager());
        filter.setAuthenticationSuccessHandler(new CustomAuthenticationSuccessHandler(sessionRegistry));
        filter.setAuthenticationFailureHandler(new CustomUrlAuthenticationFailureHandler());
        filter.setSessionAuthenticationStrategy(sessionAuthenticationStrategy(sessionRegistry));
        return filter;
    }
}
