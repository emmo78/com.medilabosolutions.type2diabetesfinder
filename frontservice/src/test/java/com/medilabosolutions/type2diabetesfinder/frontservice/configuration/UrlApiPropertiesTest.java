package com.medilabosolutions.type2diabetesfinder.frontservice.configuration;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
class UrlApiPropertiesTest {

    @Autowired
    UrlApiProperties urlApiProperties;

    @Test
    void getApiURL() {

        assertEquals("http://localhost:9090", urlApiProperties.getApiURL());
    }
}