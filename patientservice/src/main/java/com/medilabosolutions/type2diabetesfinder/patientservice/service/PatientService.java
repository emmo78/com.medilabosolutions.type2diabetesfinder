package com.medilabosolutions.type2diabetesfinder.patientservice.service;

import com.medilabosolutions.type2diabetesfinder.patientservice.model.Patient;
import org.apache.coyote.BadRequestException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.rest.webmvc.ResourceNotFoundException;

/**
 * Service interface for Patient DAL
 * @author olivi
 *
 */
public interface PatientService {

    /**
     * Return all patients by pages
     * @param pageRequest
     * @return list of patients
     * @throws NullPointerException  //if pageRequest is null
     */
    Page<Patient> getPatients(Pageable pageRequest) throws NullPointerException;

    /**
     * Return patient from a given id
     * @param id:Integer
     * @return patient
     * @throws ResourceNotFoundException
     */
    Patient getPatient(Integer id) throws ResourceNotFoundException;

       /**
     * Persist a patient
     * @param patient with id null
     * @return created patient with id not null
     * @throws BadRequestException if id is not null
     */
    Patient createPatient(Patient patient) throws BadRequestException;

    /**
     * Update a patient
     *
     * @param patient with id not null
     * @return updated patient
     * @throws ResourceNotFoundException
     */
    Patient updatePatient(Patient patient) throws ResourceNotFoundException;

    /**
     * Delete patient
     *
     * @param id
     */
    void deletePatient(Integer id);

}