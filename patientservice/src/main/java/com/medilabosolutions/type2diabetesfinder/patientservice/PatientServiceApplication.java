package com.medilabosolutions.type2diabetesfinder.patientservice;

import com.netflix.discovery.shared.transport.jersey3.Jersey3TransportClientFactories;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.PropertySource;

/**
 * Entry point for the Patient Service Application.
 * <p>
 * This class is annotated with @SpringBootApplication indicating it is a Spring Boot application.
 * The @PropertySource annotation is used to specify the location of the external properties file db.properties.
 * <p>
 * Uses the SLF4J Logging API as indicated by the @Slf4j annotation.
 */
@Slf4j
@SpringBootApplication
@PropertySource("file:${user.dir}/**/db.properties")
@EnableDiscoveryClient
public class PatientServiceApplication {

    /**
     * The main method which serves as the entry point for the Patient Service Application.
     *
     * @param args command line arguments passed to the application
     */
    public static void main(String[] args) {
        SpringApplication.run(PatientServiceApplication.class, args);
    }

    @Bean
    public Jersey3TransportClientFactories jersey3TransportClientFactories() {
        return new Jersey3TransportClientFactories();
    }

}
