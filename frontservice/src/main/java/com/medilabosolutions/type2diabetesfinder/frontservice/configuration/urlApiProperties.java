package com.medilabosolutions.type2diabetesfinder.frontservice.configuration;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix= "com.medilabosolutions.type2diabetesfinder.frontservice.apiURL")
@Getter
@Setter
public class urlApiProperties {
    private String apiURL;
}
