package com.medilabosolutions.type2diabetesfinder.frontservice.repository;

import com.medilabosolutions.type2diabetesfinder.frontservice.configuration.UrlApiProperties;
import com.medilabosolutions.type2diabetesfinder.frontservice.model.Patient;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.data.web.PagedModel;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Slf4j
@Component
@RequiredArgsConstructor
public class PatientProxy {

    private UrlApiProperties urlApiProperties;

    /**
     * Get page of patients
     * @return A PageModel of requested page patients
     */
    public PagedModel<Patient> getPatients() {

        String baseApiUrl = urlApiProperties.getApiURL();
        String getPatientsUrl = baseApiUrl + "/patients";

        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<PagedModel<Patient>> response = restTemplate.exchange(
                getPatientsUrl,
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<PagedModel<Patient>>() {}
        );

        log.debug("Get Patients call " + response.getStatusCode().toString());

        return response.getBody();
    }

    /**
     * Get a patient by the id
     * @param id The id of the patient
     * @return The patient which matches the id
     */
    public Patient getPatient(int id) {
        String baseApiUrl = urlApiProperties.getApiURL();
        String getPatientUrl = baseApiUrl + "/patients/" + id;

        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<Patient> response = restTemplate.exchange(
                getPatientUrl,
                HttpMethod.GET,
                null,
                Patient.class
        );

        log.debug("Get Patient call " + response.getStatusCode().toString());

        return response.getBody();
    }

    /**
     * Add a new patient
     * @param e A new patient (without an id)
     * @return The patient full filled (with an id)
     */
    public Patient createPatient(Patient e) {
        String baseApiUrl = urlApiProperties.getApiURL();
        String createPatientUrl = baseApiUrl + "/patients";

        RestTemplate restTemplate = new RestTemplate();
        HttpEntity<Patient> request = new HttpEntity<Patient>(e);
        ResponseEntity<Patient> response = restTemplate.exchange(
                createPatientUrl,
                HttpMethod.POST,
                request,
                Patient.class);

        log.debug("Create Patient call " + response.getStatusCode().toString());

        return response.getBody();
    }

    /**
     * Update an patient - using the PUT HTTP Method.
     * @param e Existing patient to update
     */
    public Patient updatePatient(Patient e) {
        String baseApiUrl = urlApiProperties.getApiURL();
        String updatePatientUrl = baseApiUrl + "/patients" + e.getId();

        RestTemplate restTemplate = new RestTemplate();
        HttpEntity<Patient> request = new HttpEntity<Patient>(e);
        ResponseEntity<Patient> response = restTemplate.exchange(
                updatePatientUrl,
                HttpMethod.PUT,
                request,
                Patient.class);

        log.debug("Update Patient call " + response.getStatusCode().toString());

        return response.getBody();
    }

    /**
     * Delete an patient using exchange method of RestTemplate
     * instead of delete method in order to log the response status code.
     * @param id The patient's id to delete
     */
    public void deletePatient(int id) {
        String baseApiUrl = urlApiProperties.getApiURL();
        String deletePatientUrl = baseApiUrl + "/patients/" + id;

        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<Void> response = restTemplate.exchange(
                deletePatientUrl,
                HttpMethod.DELETE,
                null,
                Void.class);

        log.debug("Delete Patient call " + response.getStatusCode().toString());
    }

}
