package com.medilabosolutions.type2diabetesfinder.patientservice.service;

import com.medilabosolutions.type2diabetesfinder.patientservice.model.Patient;
import org.apache.coyote.BadRequestException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.rest.webmvc.ResourceNotFoundException;

/**
 * PatientService is an interface that defines the operations for managing patient entities.
 */
public interface PatientService {

    /**
     * Retrieves a paginated list of patients from the system.
     *
     * @param pageRequest the pagination and sorting information
     * @return a paginated list of patients
     * @throws NullPointerException if pageRequest is null
     */
    Page<Patient> getPatients(Pageable pageRequest) throws NullPointerException;

    /**
     * Retrieves a patient by their unique identifier.
     *
     * @param id the unique identifier of the patient to retrieve
     * @return the patient with the specified id
     * @throws ResourceNotFoundException if no patient is found with the specified id
     */
    Patient getPatient(Integer id) throws ResourceNotFoundException;

       /**
        * Creates a new patient in the system.
        *
        * @param patient the patient entity to be created
        * @return the created patient entity
        * @throws BadRequestException if the patient entity has a non-null id or any validation fails
        */
    Patient createPatient(Patient patient) throws BadRequestException;

    /**
     * Updates an existing patient entity in the system.
     *
     * @param patient the patient entity with updated information
     * @return the updated patient entity
     * @throws ResourceNotFoundException if no patient is found with the specified id
     */
    Patient updatePatient(Patient patient) throws ResourceNotFoundException;

    /**
     * Delete patient
     *
     * @param id
     */
    void deletePatient(Integer id);

}