package com.medilabosolutions.type2diabetesfinder.patientservice.configuration;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.context.annotation.Configuration;

@ConfigurationProperties(prefix = "com.medilabosolutions.type2diabetesfinder.patientservice")
@Configuration
@RefreshScope
@Getter
@Setter
public class PatientPerPageProperties {
    private int patientPerPage;
}