package com.medilabosolutions.type2diabetesfinder.frontservice.controller;

import com.medilabosolutions.type2diabetesfinder.frontservice.model.Patient;
import com.medilabosolutions.type2diabetesfinder.frontservice.service.PatientFrontService;
import com.medilabosolutions.type2diabetesfinder.frontservice.service.RequestService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PagedModel;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.ModelAndView;

import java.security.Principal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

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
    public String home(@RequestParam(name = "pageNumber") Optional<String> pageNumberOpt, Model model, WebRequest request) { //Principal user
        //with Principal user get user admin ?
        int index = Integer.parseInt(pageNumberOpt.orElseGet(()-> "0"));
        Pageable pageRequest = PageRequest.of(index, 3);
        PagedModel<Patient> patientPage = patientFrontService.getPatients(pageRequest);
        log.info("{} : patient page number : {} of {}",
                requestService.requestToString(request),
                patientPage.getMetadata().number()+1,
                patientPage.getMetadata().totalPages());
        model.addAttribute("patients", patientPage);
        int lastPage = (int) patientPage.getMetadata().totalPages()-1;
        model.addAttribute("pageInterval", pageInterval(index, lastPage));
        return "home";
    }

    @GetMapping("/createpatient")
    public String createPatient(Patient patient) {
        return "formNewPatient";
    }

    @GetMapping("/updatepatient/{id}")
    public String updatePatient(@PathVariable("id") final Integer id, Model model, WebRequest request) throws HttpClientErrorException.BadRequest {
        Patient patient = patientFrontService.getPatient(id);
        // If there is a password, don't let it
        log.info("{} : {} : patient to update = {} gotten",  requestService.requestToString(request), ((ServletWebRequest) request).getHttpMethod(), patient.toString());
        model.addAttribute("patient", patient);
        return "formPatient";
    }

    @GetMapping("/deletepatient/{id}")
    public ModelAndView deletePatient(@PathVariable("id") final int id, WebRequest request) {
        patientFrontService.deletePatient(id);
        log.info("{} : {} : user = {} deleted", requestService.requestToString(request), ((ServletWebRequest) request).getHttpMethod(), id);
        return new ModelAndView("redirect:/");
    }

    @PostMapping("/savepatient")
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
    }

    /**
     * Calculation of the parameters for the creation of the page interval
     * @param index
     * @param lastPage
     * @return
     */
    private List<Integer> pageInterval(int index, int lastPage) {
        if (lastPage>=0) {
            if (index-2 <= 0) {
                return createInterval(1, lastPage+1);
            } else if (index+2 > lastPage) {
                if (lastPage-4 <= 0) {
                    return createInterval(1, lastPage+1);
                } else {
                    return createInterval(lastPage-3, lastPage+1);
                }
            } else {
                return createInterval(index-1, index+3);
            }
        } else {
            return null;
        }
    }

    /**
     * Create page interval
     * @param min
     * @param max
     * @return
     */
    private List<Integer> createInterval(int min, int max) {
        List<Integer> interval = new ArrayList<>();
        for (int i = min, j=0; i <= max && j<5; i++,j++) {
            interval.add(j, i);
        }
        return interval;
    }


}
