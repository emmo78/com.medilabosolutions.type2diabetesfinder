package com.medilabosolutions.type2diabetesfinder.frontservice.configuration;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class urlApiPropertiesTest {
    @Test
    void getApiURL() {
        urlApiProperties urlApiProperties = new urlApiProperties();

        assertEquals("http://localhost:9090", urlApiProperties.getApiURL());
    }
}