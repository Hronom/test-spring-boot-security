package com.github.hronom.test.spring.boot.security;

import com.github.hronom.test.spring.boot.security.configs.ApplicationSecurityConfig;
import com.github.hronom.test.spring.boot.security.configs.WebMvcConfig;

import org.apache.catalina.connector.Connector;
import org.apache.coyote.http11.Http11NioProtocol;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.context.embedded.EmbeddedServletContainerFactory;
import org.springframework.boot.context.embedded.tomcat.TomcatEmbeddedServletContainerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Import;
import org.springframework.core.io.ClassPathResource;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

@EnableAutoConfiguration
@ComponentScan
@Import(value = {ApplicationSecurityConfig.class, WebMvcConfig.class})
public class SampleWebSecureApplication extends WebMvcConfigurerAdapter {
    @Bean
    public EmbeddedServletContainerFactory servletContainer() {
        TomcatEmbeddedServletContainerFactory tomcat = new TomcatEmbeddedServletContainerFactory();
        tomcat.addAdditionalTomcatConnectors(createSslConnector());
        return tomcat;
    }

    private Connector createSslConnector() {
        Connector connector = new Connector("org.apache.coyote.http11.Http11NioProtocol");
        Http11NioProtocol protocol = (Http11NioProtocol) connector.getProtocolHandler();
        try {
            File keystore = getKeyStoreFile();
            File truststore = keystore;
            connector.setScheme("https");
            connector.setSecure(true);
            connector.setPort(8443);
            protocol.setSSLEnabled(true);
            protocol.setKeystoreFile(keystore.getAbsolutePath());
            protocol.setKeystorePass("changeit");
            protocol.setTruststoreFile(truststore.getAbsolutePath());
            protocol.setTruststorePass("changeit");
            protocol.setKeyAlias("apitester");
            return connector;
        } catch (IOException ex) {
            throw new IllegalStateException(
                "cant access keystore: [" + "keystore" + "] or truststore: [" + "keystore" + "]",
                ex
            );
        }
    }

    private File getKeyStoreFile() throws IOException {
        ClassPathResource resource = new ClassPathResource("keystore");
        try {
            return resource.getFile();
        } catch (Exception ex) {
            File temp = File.createTempFile("keystore", ".tmp");
            FileCopyUtils.copy(resource.getInputStream(), new FileOutputStream(temp));
            return temp;
        }
    }

    public static void main(String[] args) throws Exception {
        System.out.println(SampleWebSecureApplication.class.getSimpleName());
        new SpringApplicationBuilder(SampleWebSecureApplication.class).run(args);
    }
}
