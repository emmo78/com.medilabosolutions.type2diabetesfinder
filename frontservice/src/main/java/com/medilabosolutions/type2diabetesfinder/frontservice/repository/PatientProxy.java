package com.medilabosolutions.type2diabetesfinder.frontservice.repository;

import com.medilabosolutions.type2diabetesfinder.frontservice.model.Patient;
import jakarta.validation.Valid;
import org.apache.coyote.BadRequestException;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.util.Optional;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Max;
import jakarta.validation.ConstraintViolationException;

@FeignClient(name = "patientService", url = "localhost:9090")
public interface PatientProxy {

    //private final UrlApiProperties urlApiProperties;

    /**
     * Get page of patients
     *
     * @return A ResponseEntity containing PageModel of requested page patients
     */
    @GetMapping("/patients")
    ResponseEntity<Page<Patient>> getPatients(@RequestParam(name = "pageNumber") Optional<String> pageNumberOpt, WebRequest request) throws IllegalArgumentException;
        /*
        log.debug("Get Patients call " + response.getStatusCode().toString());
        return response.getBody();*/


    /**
     * Get a patient by the id
     *
     * @param id The id of the patient
     * @return ResponseBody containingThe patient which matches the id
     */
    @GetMapping("/patients/{id}") //Integer.MAX_VALUE = 2 147 483 647 = 2^31-1
    ResponseEntity<Patient> getPatient(@PathVariable("id") @Min(1) @Max(2147483647) Integer id, WebRequest request) throws MethodArgumentTypeMismatchException, ConstraintViolationException;//, ResourceNotFoundException;
        /*
        log.debug("Get Patient call " + response.getStatusCode().toString());
        return response.getBody();*/


    /**
     * Add a new patient
     *
     * @param newPatient A new patient (without an id)
     * @return ResponseEntity containing The patient full filled (with an id)
     */
    @PostMapping("/patients")
    ResponseEntity<Patient> createPatient(@RequestBody Optional<@Valid Patient> optionalPatient, WebRequest request) throws MethodArgumentNotValidException, BadRequestException;
        /*log.debug("Create Patient call " + response.getStatusCode().toString());

        return response.getBody();*/

    /**
     * Update an patient - using the PUT HTTP Method.
     *
     * @param optionalPatient Existing patient to update
     */
    @PutMapping("/patients")
    ResponseEntity<Patient> updatePatient(@RequestBody Optional<@Valid Patient> optionalPatient, WebRequest request) throws MethodArgumentNotValidException, BadRequestException;

        /*log.debug("Update Patient call " + response.getStatusCode().toString());
        return response.getBody();*/

    /**
     * Delete an patient using exchange method of RestTemplate
     * instead of delete method in order to log the response status code.
     *
     * @param id The patient's id to delete
     */
    @DeleteMapping("/patients/{id}")
    HttpStatus deletePatientById(@PathVariable("id") @Min(1) @Max(2147483647) Integer id, WebRequest request) throws MethodArgumentTypeMismatchException, ConstraintViolationException;
        /*String baseApiUrl = urlApiProperties.getApiURL();
        String deletePatientUrl = baseApiUrl + "/patients/" + id;

        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<Void> response = restTemplate.exchange(
                deletePatientUrl,
                HttpMethod.DELETE,
                null,
                Void.class);*/

        /*log.debug("Delete Patient call " + response.getStatusCode().toString());
        return HttpStatus.OK;*/
}
