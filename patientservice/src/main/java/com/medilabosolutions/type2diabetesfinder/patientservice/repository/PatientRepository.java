package com.medilabosolutions.type2diabetesfinder.patientservice.repository;

import com.medilabosolutions.type2diabetesfinder.patientservice.model.Patient;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface PatientRepository extends JpaRepository<Patient, Long>, JpaSpecificationExecutor<Patient> {

}
