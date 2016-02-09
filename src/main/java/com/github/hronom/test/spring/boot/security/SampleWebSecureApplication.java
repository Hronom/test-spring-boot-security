package com.github.hronom.test.spring.boot.security;

import com.github.hronom.test.spring.boot.security.configs.ApplicationSecurityConfig;
import com.github.hronom.test.spring.boot.security.configs.EmbeddedServletContainerConfig;
import com.github.hronom.test.spring.boot.security.configs.FiltersConfig;

import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.context.web.SpringBootServletInitializer;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Import;

@EnableAutoConfiguration
@ComponentScan
@Import(value = {
    ApplicationSecurityConfig.class,
    FiltersConfig.class,
    EmbeddedServletContainerConfig.class
})
public class SampleWebSecureApplication extends SpringBootServletInitializer {
    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
        return application.sources(SampleWebSecureApplication.class);
    }

    public static void main(String[] args) throws Exception {
        System.out.println(SampleWebSecureApplication.class.getSimpleName());
        new SpringApplicationBuilder(SampleWebSecureApplication.class).run(args);
    }
}
