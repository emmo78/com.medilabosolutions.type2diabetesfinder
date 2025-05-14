package com.medilabosolutions.type2diabetesfinder.frontservice.configuration;

import feign.auth.BasicAuthRequestInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

    @Configuration
    public class FeignClientConfig {

        @Bean
        public BasicAuthRequestInterceptor basicAuthRequestInterceptor() {
            return new BasicAuthRequestInterceptor("user", "user");
        }
    }