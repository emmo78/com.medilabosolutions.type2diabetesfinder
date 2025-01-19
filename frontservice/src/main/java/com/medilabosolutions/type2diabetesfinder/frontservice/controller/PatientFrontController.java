package com.medilabosolutions.type2diabetesfinder.frontservice.controller;

import com.medilabosolutions.type2diabetesfinder.frontservice.model.Patient;
import com.medilabosolutions.type2diabetesfinder.frontservice.service.PatientFrontService;
import com.medilabosolutions.type2diabetesfinder.frontservice.service.RequestService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.web.PagedModel;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.ModelAndView;

import java.util.List;

/**
 * PatientController class handles HTTP requests related to User management.
 *
 * @author olivier morel
 */
@Controller
@RequiredArgsConstructor
@Slf4j
public class PatientFrontController {

    private final PatientFrontService patientFrontService;
    private final RequestService requestService;

    @GetMapping("/")
    public String home(Model model, WebRequest request) { //Principal user
        PagedModel<Patient> patientPage = patientFrontService.getPatients();
        log.info("{} : patient page number : {} of {}",
                requestService.requestToString(request),
                patientPage.getMetadata().number()+1,
                patientPage.getMetadata().totalPages());
        model.addAttribute("patients", patientPage);
        return "home";
    }

    @GetMapping("/createpatient")
    public String createPatient(Patient patient) {
        return "formNewPatient";
    }

    @GetMapping("/updatePatient/{id}")
    public String updatePatient(@PathVariable("id") final Integer id, Model model, WebRequest request) throws HttpClientErrorException.BadRequest {
        Patient patient = patientFrontService.getPatient(id);
        // If there is a password, don't let it
        log.info("{} : {} : patient to update = {} gotten",  requestService.requestToString(request), ((ServletWebRequest) request).getHttpMethod(), patient.toString());
        model.addAttribute("patient", patient);
        return "formPatient";
    }

    @GetMapping("/deletePatient/{id}")
    public ModelAndView deletePatient(@PathVariable("id") final int id, WebRequest request) {
        patientFrontService.deletePatient(id);
        log.info("{} : {} : user = {} deleted", requestService.requestToString(request), ((ServletWebRequest) request).getHttpMethod(), id);
        return new ModelAndView("redirect:/");
    }

    @PostMapping("/savePatient")
    public ModelAndView savePatient(@ModelAttribute Patient patient, WebRequest request) throws HttpClientErrorException.BadRequest {
        Patient savedPatient;
        if(patient.getId() == null) {
            // If id is null, then it is a new patient.
            savedPatient = patientFrontService.createPatient(patient);
        } else {
            savedPatient = patientFrontService.updatePatient(patient);
        };

        log.info("{} : {} : patient = {} persisted", requestService.requestToString(request), ((ServletWebRequest) request).getHttpMethod(), savedPatient.toString());
        return new ModelAndView("redirect:/");
    }}
