package com.medilabosolutions.type2diabetesfinder.patientservice.controller;

import com.medilabosolutions.type2diabetesfinder.patientservice.model.Patient;
import com.medilabosolutions.type2diabetesfinder.patientservice.service.PatientService;
import com.medilabosolutions.type2diabetesfinder.patientservice.service.RequestService;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.coyote.BadRequestException;
import org.springframework.dao.InvalidDataAccessApiUsageException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.rest.webmvc.ResourceNotFoundException;
import org.springframework.data.web.PagedModel;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.util.Optional;

/**
 * REST controller for handling patient-related operations.
 */
@RestController
@Slf4j
@RequiredArgsConstructor
@Validated //for constraints on PathVariable
public class PatientController {

    private final PatientService patientService;
    private final RequestService requestService;

    /**
     * Retrieves a list of all patients.
     *
     * @param request the current web request containing request data
     * @return a ResponseEntity containing an Iterable of Patient objects and an HTTP status code
     * @throws NullPointerException if the pageRequest is null
     */
    // Retrieve all patients
    @GetMapping("/patients")
    public ResponseEntity<PagedModel<Patient>> getPatients(WebRequest request) throws NullPointerException {
        //todo with with front
        Pageable pageRequest = Pageable.unpaged();
        //Throw NullPointerException if pageRequest is null
        Page<Patient> patients = patientService.getPatients(pageRequest);
        log.info("{} : {} : patients page number : {} of {}",
                requestService.requestToString(request),
                ((ServletWebRequest) request).getHttpMethod(),
                patients.getNumber()+1,
                patients.getTotalPages());
        return new ResponseEntity<>(new PagedModel<>(patients), HttpStatus.OK);
    }

    /**
     * Retrieve information by patient Id.
     *
     * @param id the ID of the patient to retrieve. Must be between 1 and 2147483647.
     * @param request the web request context.
     * @return ResponseEntity containing the patient information if found, with HTTP status 200.
     * @throws MethodArgumentTypeMismatchException if the ID is null.
     * @throws ConstraintViolationException if the ID does not meet the defined constraints.
     * @throws ResourceNotFoundException if no patient is found with the provided ID.
     */
    // Retrieve information by patient Id
    //ConstraintViolationException are thrown by constraint violation on path variable
    //if id is null (Integer) throws MethodArgumentTypeMismatchException
    @GetMapping("/patients/{id}") //Integer.MAX_VALUE = 2 147 483 647 = 2^31-1
    public ResponseEntity<Patient> getPatientById(@PathVariable("id") @Min(1) @Max(2147483647) Integer id, WebRequest request) throws MethodArgumentTypeMismatchException, ConstraintViolationException, ResourceNotFoundException {
        //Throw ResourceNotFoundException if patient not found by id
        Patient patient = patientService.getPatient(id);
        log.info("{} : {} : patient = {} gotten",  requestService.requestToString(request), ((ServletWebRequest) request).getHttpMethod(), patient.toString());
        return new ResponseEntity<>(patient, HttpStatus.OK);
    }

    /**
     * Creates a new patient entry in the system.
     * This method will throw a MethodArgumentNotValidException if the provided patient details are not valid.
     *
     * @param optionalPatient the patient details wrapped in an Optional. The patient object should be valid and not null.
     * @param request the web request object representing the client's request.
     * @return ResponseEntity containing the saved patient details along with the HTTP status code.
     * @throws MethodArgumentNotValidException if the patient details provided are invalid.
     * @throws BadRequestException if the patient object is null or contains an ID.
     */
    // Create a new patient
    // Throw MethodArgumentNotValidException by @Valid in @RequestBody
    @PostMapping("/patients/")
    public ResponseEntity<Patient> createPatient(@RequestBody Optional<@Valid Patient> optionalPatient, WebRequest request) throws MethodArgumentNotValidException, BadRequestException {
        if (optionalPatient.isEmpty()) {
            throw new BadRequestException("Correct request should be a json Patient body");
        }
        //Throw BadRequestException if id not null
        Patient patientSaved = patientService.createPatient(optionalPatient.get());
        log.info("{} : {} : patient = {} persisted", requestService.requestToString(request), ((ServletWebRequest) request).getHttpMethod(), patientSaved.toString());
        return new ResponseEntity<>(patientSaved, HttpStatus.OK);
    }

    /**
     * Updates an existing patient's information.
     *
     * @param optionalPatient an optional containing the patient object to be updated, must be validated
     * @param request an instance of WebRequest containing the details of the request
     * @return ResponseEntity containing the updated patient object and HTTP status OK
     * @throws MethodArgumentNotValidException if the patient object is not valid
     * @throws BadRequestException if the request body is missing or invalid
     * @throws ResourceNotFoundException if the patient does not exist
     * @throws InvalidDataAccessApiUsageException if the patient id is null
     */
    // Update patient information
    // Throw MethodArgumentNotValidException by @Valid in @RequestBody
    @PutMapping("/patients/")
    public ResponseEntity<Patient> updatePatient(@RequestBody Optional<@Valid Patient> optionalPatient, WebRequest request) throws MethodArgumentNotValidException, BadRequestException, ResourceNotFoundException, InvalidDataAccessApiUsageException {
        if (optionalPatient.isEmpty()) {
            throw new BadRequestException("Correct request should be a json Patient body");
        }
        // Throw ResourceNotFoundException
        // Throw InvalidApiUsageException if null id
        Patient patientUpdated = patientService.updatePatient(optionalPatient.get());
        log.info("{} : {} : patient = {} persisted", requestService.requestToString(request), ((ServletWebRequest) request).getHttpMethod(), patientUpdated.toString());
        return new ResponseEntity<>(patientUpdated, HttpStatus.OK);
    }

    /**
     * Deletes a patient by their unique identifier.
     *
     * @param id the unique identifier of the patient to be deleted; must be a positive integer
     * @param request the web request object containing request details
     * @return the HTTP status indicating the outcome of the delete operation
     * @throws MethodArgumentTypeMismatchException if the provided id is not a valid integer
     * @throws ConstraintViolationException if the provided id does not meet the validation constraints
     */
    // Delete a patient
    //ConstraintViolationException are thrown by constraint violation on path variable
    //if id is null (Integer) throws MethodArgumentTypeMismatchException
    @DeleteMapping("/patients/{id}")
    public HttpStatus deletePatientById(@PathVariable("id") @Min(1) @Max(2147483647) Integer id, WebRequest request) throws MethodArgumentTypeMismatchException, ConstraintViolationException {
        patientService.deletePatient(id);
        log.info("{} : {} : user = {} deleted", requestService.requestToString(request), ((ServletWebRequest) request).getHttpMethod(), id);
        return HttpStatus.OK;
    }
}
