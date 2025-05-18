package com.medilabosolutions.type2diabetesfinder.frontservice.service;

import com.medilabosolutions.type2diabetesfinder.frontservice.model.Note;
import com.medilabosolutions.type2diabetesfinder.frontservice.model.Patient;
import com.medilabosolutions.type2diabetesfinder.frontservice.repository.PatientProxy;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.coyote.BadRequestException;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.context.request.WebRequest;

import java.util.Optional;

/**
 * Implementation of the PatientService interface for managing patient entities.
 * This service provides methods to retrieve, create, update, and delete patients.
 */
@Service
@Slf4j
@AllArgsConstructor
public class PatientFrontServiceImpl implements PatientFrontService {

    private final PatientProxy patientProxy;

    /**
     * Retrieves a page of patients based on the provided pagination information.
     *
     * @return A page of patients based on the given pagination information.
     */
    @Override
    public Page<Patient> getPatients(int index) {
        return patientProxy.getPatients(Optional.of(String.valueOf(index))).getBody();
    }

    /**
     * Retrieves a patient by their unique identifier.
     *
     * @param id the unique identifier of the patient to be retrieved
     * @return the patient corresponding to the given identifier
     * @throws HttpClientErrorException.BadRequest if no patient is found with the given identifier
     */
    @Override
    public Patient getPatient(Integer id) throws HttpClientErrorException.BadRequest {
        Patient patient = patientProxy.getPatient(id).getBody();
        patient.setMedicalNotes(patientProxy.getNotesByPatientId(id).getBody());
        return patient;
    }

    /**
     * Add a new patient entity in the repository.
     *
     * @param patient the patient entity to be created
     * @return the created patient entity
     * @throws HttpClientErrorException.BadRequest if the patient entity has a non-null ID
     */
    @Override
    public Patient createPatient(Patient patient) throws HttpClientErrorException.BadRequest {
        return patientProxy.createPatient(Optional.ofNullable(patient)).getBody();
    }

    /**
     * Updates an existing patient in the repository.
     *
     * @param patient the patient entity to be updated
     * @return the updated patient entity
     * @throws HttpClientErrorException.BadRequest if the provided patient entity has a null identifier
     */

    @Override
    public Patient updatePatient(Patient patient) throws HttpClientErrorException.BadRequest {
        return patientProxy.updatePatient(Optional.ofNullable(patient)).getBody();
    }

    /**
     * Deletes a patient with the given ID. If the patient does not exist, the request is silently ignored.
     *
     * @param id the ID of the patient to be deleted. Must not be null.
     */
    @Override
    public HttpStatus deletePatient(Integer id) {
        // If the entity is not found in the persistence store it is silently ignored.
        return patientProxy.deletePatientById(id);
    }

    /**
     *
     * @param note the patient entity to be created
     * @return
     * @throws HttpClientErrorException.BadRequest
     */
    @Override
    public Note createNote(Note note) throws HttpClientErrorException.BadRequest {
        return patientProxy.createNote(Optional.ofNullable(note)).getBody();


    }

}