package com.medilabosolutions.type2diabetesfinder.patientservice.controller;

import com.medilabosolutions.type2diabetesfinder.patientservice.model.Patient;
import com.medilabosolutions.type2diabetesfinder.patientservice.service.PatientService;
import com.medilabosolutions.type2diabetesfinder.patientservice.service.RequestService;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.coyote.BadRequestException;
import org.springframework.dao.InvalidDataAccessApiUsageException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.rest.webmvc.ResourceNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.context.request.WebRequest;

import java.util.Optional;

/**
 * PatientController class represents the API endpoints (CRUD) for managing Patient.
 *
 * @author olivier morel
 */
@RestController
@AllArgsConstructor
@Slf4j
@Validated //for constraints on PathVariable
public class PatientController {

    private final PatientService patientService;
    private final RequestService requestService;

    @GetMapping("/patients")
    public ResponseEntity<Iterable<Patient>> getPatients(WebRequest request) throws NullPointerException {
        //todo with with front
        Pageable pageRequest = Pageable.unpaged();
        //Throw NullPointerException if pageRequest is null
        Page<Patient> patients = patientService.getPatients(pageRequest);
        log.info("{} : {} : patients page number : {} of {}",
                requestService.requestToString(request),
                ((ServletWebRequest) request).getHttpMethod(),
                patients.getNumber()+1,
                patients.getTotalPages());
        return new ResponseEntity<>(patients, HttpStatus.OK);
    }

    //todo : test id null (Integer), Constraint ?
    @GetMapping("/patient/{id}") //Integer.MAX_VALUE = 2 147 483 647 = 2^31-1
    public ResponseEntity<Patient> getPatientById(@PathVariable("id") @Min(1) @Max(2147483647) Integer id, WebRequest request) throws ConstraintViolationException, ResourceNotFoundException {
        //Throw RessourceNotfoundException
        Patient patient = patientService.getPatient(id);
        log.info("{} : {} : patient = {} gotten",  requestService.requestToString(request), ((ServletWebRequest) request).getHttpMethod(), patient.toString());
        return new ResponseEntity<>(patient, HttpStatus.OK);
    }

    //todo MethodArgumentNotValidException
    @PostMapping("/api/user/create")
    public ResponseEntity<Patient> createPatient(@RequestBody Optional<@Valid Patient> optionalPatient, WebRequest request) throws MethodArgumentNotValidException, BadRequestException {
        if (optionalPatient.isEmpty()) {
            throw new BadRequestException("Correct request should be a json Patient body");
        }
        //Throw BadRequestException if id not null
        Patient patientSaved = patientService.createPatient(optionalPatient.get());
        log.info("{} : {} : patient = {} persisted", requestService.requestToString(request), ((ServletWebRequest) request).getHttpMethod(), patientSaved.toString());
        return new ResponseEntity<>(patientSaved, HttpStatus.OK);
    }

    @PutMapping("/api/user/update")
    public ResponseEntity<Patient> updatePatient(@RequestBody Optional<@Valid Patient> optionalPatient, WebRequest request) throws MethodArgumentNotValidException, BadRequestException, ResourceNotFoundException {
        if (optionalPatient.isEmpty()) {
            throw new BadRequestException("Correct request should be a json Patient body");
        }
        // Throw ResourceNotFoundException
        //Todo InvalidApiUsageException
        Patient patientUpdated = patientService.updatePatient(optionalPatient.get());
        log.info("{} : {} : patient = {} persisted", requestService.requestToString(request), ((ServletWebRequest) request).getHttpMethod(), patientUpdated.toString());
        return new ResponseEntity<>(patientUpdated, HttpStatus.OK);
    }

    @DeleteMapping("/api/user/delete/{id}")
    //Todo InvalidDataAccessApiUsageException and Test
    public HttpStatus deletePatientById(@PathVariable("id") @Min(1) @Max(2147483647) Integer id, WebRequest request) throws ConstraintViolationException {
        patientService.deletePatient(id);
        log.info("{} : {} : user = {} deleted", requestService.requestToString(request), ((ServletWebRequest) request).getHttpMethod(), id);
        return HttpStatus.OK;
    }









}
