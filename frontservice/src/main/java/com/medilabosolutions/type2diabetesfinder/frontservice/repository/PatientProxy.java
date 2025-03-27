package com.medilabosolutions.type2diabetesfinder.frontservice.repository;

import com.medilabosolutions.type2diabetesfinder.frontservice.model.Patient;
import feign.Body;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.util.Optional;

@FeignClient(name = "patientService" , url = "localhost:9090")
public interface PatientProxy {

    //private final UrlApiProperties urlApiProperties;

    /**
     * Get page of patients
     *
     * @return A ResponseEntity containing PageModel of requested page patients
     */
    @GetMapping("/patients")
    ResponseEntity<Page<Patient>> getPatients(@RequestParam(name = "pageNumber") Optional<String> pageNumberOpt); //throws IllegalArgumentException;

    /**
     * Get a patient by the id
     *
     * @param id The id of the patient
     * @return ResponseBody containingThe patient which matches the id
     */
    @GetMapping("/patients/{id}")
    //Integer.MAX_VALUE = 2 147 483 647 = 2^31-1
    ResponseEntity<Patient> getPatient(@PathVariable("id") @Min(1) @Max(2147483647) Integer id); //throws MethodArgumentTypeMismatchException, ConstraintViolationException, ResourceNotFoundException;

    /**
     * Add a new patient
     *
     * @param optionalPatient contains a new patient (without an id)
     * @return ResponseEntity containing The patient fulfilled (with an id)
     */
    @PostMapping("/patients")
    // json curly braces must be escaped!
    @Body("%7B\"optionalPatient\": \"{optionalPatient}\"%7D")
    ResponseEntity<Patient> createPatient(@RequestBody Optional<@Valid Patient> optionalPatient); //throws MethodArgumentNotValidException, BadRequestException;

    /**
     * Update an patient - using the PUT HTTP Method.
     *
     * @param optionalPatient Existing patient to update
     */
    @PutMapping("/patients")
    ResponseEntity<Patient> updatePatient(@RequestBody Optional<@Valid Patient> optionalPatient); //throws MethodArgumentNotValidException, BadRequestException;

    /**
     * Delete an patient using exchange method of RestTemplate
     * instead of delete method in order to log the response status code.
     *
     * @param id The patient's id to delete
     */
    @DeleteMapping("/patients/{id}")
    HttpStatus deletePatientById(@PathVariable("id") @Min(1) @Max(2147483647) Integer id) throws MethodArgumentTypeMismatchException, ConstraintViolationException;
}
