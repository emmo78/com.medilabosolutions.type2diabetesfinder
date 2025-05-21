package com.medilabosolutions.type2diabetesfinder.frontservice.configuration;

import feign.auth.BasicAuthRequestInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

    /**
     * Configuration class for Feign client within the application.
     *
     * This class provides the necessary configuration for authenticating
     * Feign-based HTTP requests using Basic Authentication. It acts as a
     * centralized point for defining authentication-related settings.
     *
     * An instance of {@link BasicAuthRequestInterceptor} is provided as a
     * Spring-managed bean to intercept and enrich HTTP requests made through
     * Feign clients with Basic Authentication credentials.
     *
     * Bean Definitions:
     * - basicAuthRequestInterceptor: Configures the Feign client to use
     *   Basic Authentication with predefined credentials.
     *
     * This configuration can be used by Feign clients to communicate with
     * external systems or APIs that require Basic Authentication for access
     * control.
     *
     * @author olivier morel
     */
    @Configuration
    public class FeignClientConfig {

        @Bean
        public BasicAuthRequestInterceptor basicAuthRequestInterceptor() {
            return new BasicAuthRequestInterceptor("user", "user");
        }
    }