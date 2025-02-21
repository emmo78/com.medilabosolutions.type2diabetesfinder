package com.medilabosolutions.type2diabetesfinder.patientservice;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@Slf4j
@SpringBootTest
@ActiveProfiles("mytest")
class PatientServiceApplicationTests {

    @Test
    void contextLoads() {
        log.info("PatientServiceApplicationTests");
    }

}
