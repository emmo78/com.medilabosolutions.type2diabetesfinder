package com.medilabosolutions.type2diabetesfinder.frontservice;

import com.netflix.discovery.shared.transport.jersey3.Jersey3TransportClientFactories;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
@EnableFeignClients("com.medilabosolutions.type2diabetesfinder.frontservice")
@EnableDiscoveryClient
public class FrontServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(FrontServiceApplication.class, args);
    }

    @Bean
    public Jersey3TransportClientFactories jersey3TransportClientFactories() {
        return new Jersey3TransportClientFactories();
    }
}
