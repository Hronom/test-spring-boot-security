package com.github.hronom.test.spring.boot.security.session.configs;

import com.github.hronom.test.spring.boot.security.session.configs.custom.objects.CustomAuthenticationProvider;
import com.github.hronom.test.spring.boot.security.session.configs.custom.objects.RestAuthenticationEntryPoint;
import com.github.hronom.test.spring.boot.security.session.filters.CustomUsernamePasswordAuthenticationFilter;
import com.github.hronom.test.spring.boot.security.session.handlers.CustomAccessDeniedHandler;
import com.github.hronom.test.spring.boot.security.session.handlers.CustomAuthenticationSuccessHandler;
import com.github.hronom.test.spring.boot.security.session.handlers.CustomLogoutSuccessHandler;
import com.github.hronom.test.spring.boot.security.session.handlers.CustomUrlAuthenticationFailureHandler;

import org.springframework.boot.autoconfigure.security.SecurityProperties;
import org.springframework.boot.context.embedded.ServletListenerRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.security.core.session.SessionRegistryImpl;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.session.CompositeSessionAuthenticationStrategy;
import org.springframework.security.web.authentication.session.ConcurrentSessionControlAuthenticationStrategy;
import org.springframework.security.web.authentication.session.RegisterSessionAuthenticationStrategy;
import org.springframework.security.web.authentication.session.SessionAuthenticationStrategy;
import org.springframework.security.web.authentication.session.SessionFixationProtectionStrategy;
import org.springframework.security.web.session.HttpSessionEventPublisher;
import org.springframework.security.web.session.SessionManagementFilter;

@Configuration
@EnableGlobalMethodSecurity(securedEnabled = true)
@Order(SecurityProperties.ACCESS_OVERRIDE_ORDER)
public class ApplicationSecurityConfig extends WebSecurityConfigurerAdapter {
    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
            .requiresChannel()
            .antMatchers("/api/**").requiresSecure()
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
            .maximumSessions(1)
            .expiredUrl("/login?expired")
            .sessionRegistry(sessionRegistry())
            .and()
            //.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            //.sessionFixation().migrateSession()
            .and()
            .httpBasic().authenticationEntryPoint(restAuthenticationEntryPoint())
            .and()
            .exceptionHandling().accessDeniedHandler(new CustomAccessDeniedHandler())
            .and()
            .formLogin().disable()
            .logout()
            .logoutUrl("/api/logout").permitAll()
            .logoutSuccessHandler(new CustomLogoutSuccessHandler())
            .and()
            // Disable CSRF for making /logout available for all HTTP methods (POST, GET...)
            .csrf()
            .disable();

        http.addFilterBefore(
            customUsernamePasswordAuthenticationFilter(),
            UsernamePasswordAuthenticationFilter.class
        );
    }

    @Override
    public void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.authenticationProvider(new CustomAuthenticationProvider());
    }

    @Bean
    public SessionRegistry sessionRegistry() throws Exception {
        return new SessionRegistryImpl();
    }

    /**
     * Register HttpSessionEventPublisher. Note that it is declared
     * static to instantiate it very early, before this configuration
     * class is processed.
     *
     * See http://docs.spring.io/spring-boot/docs/current/reference/html/howto-embedded-servlet-containers.html
     * for how to add a ServletContextListener.
     *
     * See http://docs.spring.io/spring/docs/current/javadoc-api/org/springframework/context/annotation/Bean.html
     * for how static instantiation works.
     */
    @Bean
    public static ServletListenerRegistrationBean<HttpSessionEventPublisher> httpSessionEventPublisher() {
        return new ServletListenerRegistrationBean<>(new HttpSessionEventPublisher());
    }

    @Bean
    public RestAuthenticationEntryPoint restAuthenticationEntryPoint() throws Exception {
        return new RestAuthenticationEntryPoint();
    }

    @Bean
    public CustomUsernamePasswordAuthenticationFilter customUsernamePasswordAuthenticationFilter() throws Exception {
        CustomUsernamePasswordAuthenticationFilter filter =
            new CustomUsernamePasswordAuthenticationFilter("/api/login");
        filter.setAuthenticationManager(authenticationManager());
        filter.setAuthenticationSuccessHandler(new CustomAuthenticationSuccessHandler());
        filter.setAuthenticationFailureHandler(new CustomUrlAuthenticationFailureHandler());
        return filter;
    }
}
