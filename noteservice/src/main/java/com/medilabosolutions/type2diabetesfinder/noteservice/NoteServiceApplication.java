package com.medilabosolutions.type2diabetesfinder.noteservice;

import com.netflix.discovery.shared.transport.jersey3.Jersey3TransportClientFactories;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.PropertySource;

/**
 * Entry point for the Note Service Application.
 * <p>
 * This class is annotated with @SpringBootApplication indicating it is a Spring Boot application.
 * The @PropertySource annotation is used to specify the location of the external properties file mongodb.properties.
 * <p>
 * Uses the SLF4J Logging API as indicated by the @Slf4j annotation.
 */
@SpringBootApplication
@PropertySource("file:${user.dir}/**/mongodb.properties")
@EnableDiscoveryClient
public class NoteServiceApplication {

    /**
     * The main method which serves as the entry point for the Note Service Application.
     *
     * @param args command line arguments passed to the application
     */
    public static void main(String[] args) {
        SpringApplication.run(NoteServiceApplication.class, args);
    }

    @Bean
    public Jersey3TransportClientFactories jersey3TransportClientFactories() {
        return new Jersey3TransportClientFactories();
    }
}