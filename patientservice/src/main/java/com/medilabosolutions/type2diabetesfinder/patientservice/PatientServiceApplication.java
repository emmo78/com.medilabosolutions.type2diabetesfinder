package com.medilabosolutions.type2diabetesfinder.patientservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.PropertySource;

@SpringBootApplication
@PropertySource({"file:./patientservice/db.properties"})
public class PatientServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(PatientServiceApplication.class, args);
	}

}
