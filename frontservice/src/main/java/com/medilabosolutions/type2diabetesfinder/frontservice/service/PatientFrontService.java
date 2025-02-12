package com.medilabosolutions.type2diabetesfinder.frontservice.service;

import com.medilabosolutions.type2diabetesfinder.frontservice.model.Patient;
import org.springframework.data.domain.Page;
import org.springframework.web.client.HttpClientErrorException;

/**
 * PatientService is an interface that defines the operations for managing patient entities.
 */
public interface PatientFrontService {

    /**
     * Retrieves a paginated list of patients from the system.
     *
     * @return a paginated list of patients
     */
    Page<Patient> getPatients(int index);

    /**
     * Retrieves a patient by their unique identifier.
     *
     * @param id the unique identifier of the patient to retrieve
     * @return the patient with the specified id
     * @throws HttpClientErrorException.BadRequest if no patient is found with the specified id
     */
    Patient getPatient(Integer id) throws HttpClientErrorException.BadRequest;

       /**
        * Creates a new patient in the system.
        *
        * @param patient the patient entity to be created
        * @return the created patient entity
        * @throws HttpClientErrorException.BadRequest if the patient entity has a non-null id or any validation fails
        */
    Patient createPatient(Patient patient) throws HttpClientErrorException.BadRequest;

    /**
     * Updates an existing patient entity in the system.
     *
     * @param patient the patient entity with updated information
     * @return the updated patient entity
     * @throws HttpClientErrorException.BadRequest if no patient is found with the specified id
     */
    Patient updatePatient(Patient patient) throws HttpClientErrorException.BadRequest;

    /**
     * Deletes a patient with the given ID. If the patient does not exist, the request is silently ignored.
     *
     * @param id the ID of the patient to be deleted. Must not be null.
     */
    void deletePatient(Integer id);

}