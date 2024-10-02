package com.medilabosolutions.type2diabetesfinder.patientservice.service;

import com.medilabosolutions.type2diabetesfinder.patientservice.exception.ResourceConflictException;
import com.medilabosolutions.type2diabetesfinder.patientservice.model.Patient;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.UnexpectedRollbackException;
import org.springframework.web.context.request.WebRequest;

/**
 * Service interface for Patient DAL
 * @author olivi
 *
 */
public interface PatientService {


    /**
     * Persist a patient
     *
     * @param patient with id null
     * @return created patient
     * @throws ResourceConflictException
     * @throws UnexpectedRollbackException
     */
    Patient createPatient(Patient patient) throws ResourceConflictException, UnexpectedRollbackException;

    /**
     * Return patient from a given id
     *
     * @param id: Integer
     * @return patient
     * @throws UnexpectedRollbackException
     */
    Patient getPatient(Integer id) throws UnexpectedRollbackException;

    /**
     * Update a patient
     *
     * @param patient
     * @return updated patient
     * @throws UnexpectedRollbackException
     */
    Patient updatePatient(Patient patient) throws UnexpectedRollbackException;

    /**
     * Delete patient
     *
     * @param id
     * @throws UnexpectedRollbackException
     */
    void deletePatient(Integer id) throws UnexpectedRollbackException;

    /**
     * Return all patients
     * @param pageRequest
     * @return list of patients
     * @throws UnexpectedRollbackException
     */
    Page<Patient> getPatients(Pageable pageRequest) throws UnexpectedRollbackException;
}