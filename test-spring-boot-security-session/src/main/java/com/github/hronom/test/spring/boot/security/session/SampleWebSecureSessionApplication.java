package com.github.hronom.test.spring.boot.security.session;

import com.github.hronom.test.spring.boot.security.session.configs.AppConfig;
import com.github.hronom.test.spring.boot.security.session.configs.ApplicationSecurityConfig;
import com.github.hronom.test.spring.boot.security.session.configs.EmbeddedServletContainerConfig;
import com.github.hronom.test.spring.boot.security.session.configs.FiltersConfig;

import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.context.web.SpringBootServletInitializer;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Import;

@EnableAutoConfiguration
@ComponentScan
@Import(value = {
    AppConfig.class,
    ApplicationSecurityConfig.class,
    FiltersConfig.class,
    EmbeddedServletContainerConfig.class
})
public class SampleWebSecureSessionApplication extends SpringBootServletInitializer {
    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
        return application.sources(SampleWebSecureSessionApplication.class);
    }

    public static void main(String[] args) throws Exception {
        System.out.println(SampleWebSecureSessionApplication.class.getSimpleName());
        new SpringApplicationBuilder(SampleWebSecureSessionApplication.class).run(args);
    }
}
