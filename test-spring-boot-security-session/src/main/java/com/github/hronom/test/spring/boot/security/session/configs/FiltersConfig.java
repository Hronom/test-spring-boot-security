package com.github.hronom.test.spring.boot.security.session.configs;

import com.github.hronom.test.spring.boot.security.session.filters.SimpleCORSFilter;

import org.springframework.boot.context.embedded.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Conventions;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

import java.util.EnumSet;

import javax.servlet.DispatcherType;
import javax.servlet.Filter;

@Configuration
public class FiltersConfig extends WebMvcConfigurerAdapter {
    @Bean
    public FilterRegistrationBean simpleCORSFilterRegistration() {
        Filter filter = new SimpleCORSFilter();

        FilterRegistrationBean registration = new FilterRegistrationBean();
        registration.setFilter(filter);
        registration.setName(Conventions.getVariableName(filter));
        registration.setAsyncSupported(true);
        registration.setDispatcherTypes(EnumSet.allOf(DispatcherType.class));
        registration.setMatchAfter(false);
        registration.addUrlPatterns("/*");

        return registration;
    }
}
