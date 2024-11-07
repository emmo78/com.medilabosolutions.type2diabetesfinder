package com.medilabosolutions.type2diabetesfinder.patientservice.repository;

import com.medilabosolutions.type2diabetesfinder.patientservice.model.Patient;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

/**
 * PatientRepository is a Data Access Object (DAO) interface for managing Patient entities.
 * It extends JpaRepository to provide basic CRUD operations and JpaSpecificationExecutor to support complex queries.
 */
public interface PatientRepository extends JpaRepository<Patient, Integer>, JpaSpecificationExecutor<Patient> {

}
