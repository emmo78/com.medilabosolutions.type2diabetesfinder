package com.medilabosolutions.type2diabetesfinder.frontservice.controller;

import com.medilabosolutions.type2diabetesfinder.frontservice.model.Note;
import com.medilabosolutions.type2diabetesfinder.frontservice.model.Patient;
import com.medilabosolutions.type2diabetesfinder.frontservice.service.PatientFrontService;
import com.medilabosolutions.type2diabetesfinder.frontservice.service.RequestService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.context.request.WebRequest;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * The PatientFrontController handles all interactions related to the front-end interface
 * for managing patient-related actions and views. Functions include displaying
 * patient lists, creating, updating, and deleting patients, and managing notes.
 *
 * This controller uses Thymeleaf templates for rendering views and handles
 * HTTP requests corresponding to various CRUD operations for patients and their notes.
 */
@Controller
@RequiredArgsConstructor
@Slf4j
public class PatientFrontController {

    private final PatientFrontService patientFrontService;
    private final RequestService requestService;

    @GetMapping({"/front","/front/"})
    public String homePage() {
        return "redirect:/front/home";
    }

    /**
     * Handles HTTP GET requests to the "/front/home" endpoint.
     * Retrieves patient data for the specified page, calculates pagination intervals,
     * and populates the model with required attributes for displaying the home page.
     *
     * @param pageNumberOpt an {@link Optional} containing the requested page number as a string;
     *                      if absent, defaults to "0".
     * @param model         the {@link Model} used to add attributes for the view.
     * @param request       the {@link WebRequest} object representing the current HTTP request.
     * @return a {@link String} representing the name of the Thymeleaf template to render, i.e., "home".
     * @throws NumberFormatException if the provided page number cannot be parsed as an integer.
     */
    @GetMapping("/front/home")
    public String home(@RequestParam(name = "pageNumber") Optional<String> pageNumberOpt, Model model, WebRequest request) throws NumberFormatException { //Principal user
        int index = Integer.parseInt(pageNumberOpt.orElseGet(() -> "0"));
        Page<Patient> patientPage = patientFrontService.getPatients(index);
        log.info("{} : patient page number : {} of {}",
                requestService.requestToString(request),
                patientPage.getNumber() + 1,
                patientPage.getTotalPages());
        model.addAttribute("patients", patientPage);
        int lastPage = (int) patientPage.getTotalPages() - 1;
        model.addAttribute("pageInterval", pageInterval(index, lastPage));
        return "home";
    }

    /**
     * Handles the GET request to display the form for creating a new patient.
     *
     * @param patient an instance of the Patient object, used to hold form data during submission.
     * @return the name of the HTML form view to be rendered for creating a new patient.
     */
    @GetMapping("/front/createpatient")
    public String createPatient(Patient patient) {
        return "formnewpatient";
    }

    /**
     * Handles the update of a patient by fetching the patient's data,
     * preparing it for editing, and rendering the update patient form.
     *
     * @param id the unique identifier of the patient to be updated
     * @param model the Model object used to supply attributes to the view
     * @param request the WebRequest object capturing details about the HTTP request
     * @return the name of the Thymeleaf template for the patient update form
     * @throws HttpClientErrorException.BadRequest if the request is malformed or contains invalid data
     */
    @GetMapping("/front/updatepatient/{id}")
    public String updatePatient(@PathVariable("id") final Integer id, Model model, WebRequest request) throws HttpClientErrorException.BadRequest {
        Patient patient = patientFrontService.getPatient(id);
        // If there is a password, don't let it
        log.info("{} : {} : patient to update = {} gotten", requestService.requestToString(request), ((ServletWebRequest) request).getHttpMethod(), patient.toString());
        model.addAttribute("patient", patient);
        return "formupdatepatient";
    }

    /**
     * Deletes a patient with the specified ID.
     * Logs the request details and redirects to the home page afterwards.
     *
     * @param id the ID of the patient to be deleted
     * @param request the web request containing details about the current request
     * @return a string representing the redirection path to the home page
     */
    @GetMapping("/front/deletepatient/{id}")
    public String deletePatient(@PathVariable("id") final int id, WebRequest request) {
        patientFrontService.deletePatient(id);
        log.info("{} : {} : user = {} deleted", requestService.requestToString(request), ((ServletWebRequest) request).getHttpMethod(), id);
        return "redirect:/front/home";
    }

    /**
     * Saves a `Patient` entity by either creating a new patient record or updating an existing one
     * based on the presence of an ID. Redirects to the home page upon successful operation.
     *
     * @param patient the patient object that contains the details of the patient to be saved.
     *                If the ID is null, a new patient will be created; otherwise, an existing record will be updated.
     * @param request the web request object that contains details of the current HTTP request.
     * @return a string representing the URL for redirection to the home page (typically "redirect:/front/home").
     * @throws HttpClientErrorException.BadRequest if the data provided for the patient is invalid or incomplete.
     */
    @PostMapping("/front/savepatient")
    public String savePatient(@ModelAttribute Patient patient, WebRequest request) throws HttpClientErrorException.BadRequest {
        Patient savedPatient;
        if (patient.getId() == null) {
            // If id is null, then it is a new patient.
            savedPatient = patientFrontService.createPatient(patient);
        } else {
            savedPatient = patientFrontService.updatePatient(patient);
        }

        log.info("{} : {} : patient = {} persisted", requestService.requestToString(request), ((ServletWebRequest) request).getHttpMethod(), savedPatient.toString());
        return "redirect:/front/home";
    }

    /**
     * Handles the creation of a new note for a specified patient and adds the necessary attributes to the model.
     *
     * @param id The ID of the patient for whom the note is being created, obtained from the path variable.
     * @param note The note object that will be linked to the specified patient.
     * @param request A WebRequest containing additional request information.
     * @param model A Model object used to pass attributes to the view.
     * @return A String representing the view name for the form to create a new note.
     */
    @GetMapping("/front/createnote/{idPatient}")
    public String createNote(@PathVariable("idPatient") final Integer id, Note note, WebRequest request, Model model) {
        note.setPatientId(id);
        model.addAttribute("note", note);
        return "formnewnote";
    }

    /**
     * Handles the process of saving a note. If the note does not have an ID, it creates a new note.
     * Otherwise, logic for updating an existing note would be implemented.
     *
     * @param note the Note object to be saved, containing details such as patient ID, content, etc.
     * @param request the WebRequest object providing information about the current HTTP request
     * @return a redirect URL to the update patient page for the associated note's patient
     * @throws HttpClientErrorException.BadRequest if there is an issue with the provided data
     */
    @PostMapping("/front/savenote")
    public String saveNote(@ModelAttribute Note note, WebRequest request) throws HttpClientErrorException.BadRequest {
        Note savedNote = null;
        if (note.getDateTime() == null) {
            note.setDateTime(LocalDateTime.now());
        }
        if (note.getId() == null) {
            // If id is null, then it is a new note.
            savedNote = patientFrontService.createNote(note);
        } else {
            // ToDo savedNote = patientFrontService.updateNote(note);
        }

        log.info("{} : {} : note = {} persisted", requestService.requestToString(request), ((ServletWebRequest) request).getHttpMethod(), savedNote.toString());
        return "redirect:/front/updatepatient/" + note.getPatientId() ;
    }


    /**
     * Calculation of the parameters for the creation of the page interval
     *
     * @param index
     * @param lastPage
     * @return
     */
    private List<Integer> pageInterval(Integer index, Integer lastPage) {
        if (lastPage >= 0) {
            if (index - 2 <= 0) {
                return createInterval(1, lastPage + 1);
            } else if (index + 2 > lastPage) {
                if (lastPage - 4 <= 0) {
                    return createInterval(1, lastPage + 1);
                } else {
                    return createInterval(lastPage - 3, lastPage + 1);
                }
            } else {
                return createInterval(index - 1, index + 3);
            }
        } else {
            return null;
        }
    }

    /**
     * Create page interval
     *
     * @param min
     * @param max
     * @return
     */
    private List<Integer> createInterval(int min, int max) {
        List<Integer> interval = new ArrayList<>();
        for (int i = min, j = 0; i <= max && j < 5; i++, j++) {
            interval.add(j, i);
        }
        return interval;
    }
}
