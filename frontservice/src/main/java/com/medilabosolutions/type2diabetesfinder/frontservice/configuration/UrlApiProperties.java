package com.medilabosolutions.type2diabetesfinder.frontservice.configuration;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@ConfigurationProperties(prefix = "com.medilabosolutions.type2diabetesfinder.frontservice")
@Component
@Getter
@Setter
public class UrlApiProperties {
    private String apiURL;
}