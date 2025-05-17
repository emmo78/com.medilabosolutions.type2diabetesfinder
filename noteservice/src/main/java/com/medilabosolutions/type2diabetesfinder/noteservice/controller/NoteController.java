package com.medilabosolutions.type2diabetesfinder.noteservice.controller;

import com.medilabosolutions.type2diabetesfinder.noteservice.exception.RessourceNotFoundException;
import com.medilabosolutions.type2diabetesfinder.noteservice.model.Note;
import com.medilabosolutions.type2diabetesfinder.noteservice.service.NoteService;
import com.medilabosolutions.type2diabetesfinder.noteservice.service.RequestService;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.coyote.BadRequestException;
import org.springframework.data.rest.webmvc.ResourceNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.util.List;
import java.util.Optional;

/**
 * REST controller for handling note-related operations.
 */
@RestController
@Slf4j
@RequiredArgsConstructor
@Validated
public class NoteController {

    private final NoteService noteService;
    private final RequestService requestService;

    /**
     * Récupère une note par son identifiant unique.
     *
     * @param id l'identifiant de la note à récupérer
     * @param request l'objet de requête web contenant les détails de la requête
     * @return ResponseEntity contenant la note trouvée avec un statut HTTP 200
     * @throws ResourceNotFoundException si aucune note n'est trouvée avec l'identifiant fourni
     */
    @GetMapping("/notes/{id}")
    public ResponseEntity<Note> getNoteById(@PathVariable("id") String id, WebRequest request) throws ResourceNotFoundException {
        Note note = noteService.getNote(id);
        log.info("{} : {} : note récupérée avec l'ID {}",
                requestService.requestToString(request),
                ((ServletWebRequest) request).getHttpMethod(),
                id);
        return new ResponseEntity<>(note, HttpStatus.OK);
    }

    /**
     * Retrieves all notes for a specific patient, ordered by date time in descending order.
     *
     * @param patientId the ID of the patient
     * @param request the web request context
     * @return ResponseEntity containing a list of notes for the patient, with HTTP status 200
     * @throws MethodArgumentTypeMismatchException if the patient ID is not a valid integer
     * @throws ConstraintViolationException if the patient ID does not meet the defined constraints
     */
    @GetMapping("/notes/patient/{patientId}")
    public ResponseEntity<List<Note>> getNotesByPatientId(@PathVariable("patientId") @Min(1) @Max(2147483647) Integer patientId, WebRequest request) throws MethodArgumentTypeMismatchException, ConstraintViolationException, RessourceNotFoundException {
        List<Note> notes = noteService.getNotesByPatientId(patientId);
        log.info("{} : {} : {} notes found for patient ID {}", 
                requestService.requestToString(request), 
                ((ServletWebRequest) request).getHttpMethod(),
                notes.size(),
                patientId);
        return new ResponseEntity<>(notes, HttpStatus.OK);
    }

    /**
     * Creates a new note.
     *
     * @param optionalNote the note details wrapped in an Optional
     * @param request the web request object
     * @return ResponseEntity containing the saved note details along with the HTTP status code
     * @throws MethodArgumentNotValidException if the note details provided are invalid
     * @throws BadRequestException if the note object is null or contains an ID
     */
    @PostMapping("/notes")
    public ResponseEntity<Note> createNote(@RequestBody Optional<@Valid Note> optionalNote, WebRequest request) throws MethodArgumentNotValidException, BadRequestException {
        if (optionalNote.isEmpty()) {
            throw new BadRequestException("Correct request should be a json Note body");
        }
        Note noteSaved = noteService.createNote(optionalNote.get());
        log.info("{} : {} : note = {} persisted", requestService.requestToString(request), ((ServletWebRequest) request).getHttpMethod(), noteSaved.toString());
        return new ResponseEntity<>(noteSaved, HttpStatus.CREATED);
    }

    /**
     * Updates an existing note.
     *
     * @param optionalNote an optional containing the note object to be updated
     * @param request an instance of WebRequest containing the details of the request
     * @return ResponseEntity containing the updated note object and HTTP status OK
     * @throws MethodArgumentNotValidException if the note object is not valid
     * @throws BadRequestException if the request body is missing or invalid
     * @throws ResourceNotFoundException if the note does not exist
     */
    @PutMapping("/notes")
    public ResponseEntity<Note> updateNote(@RequestBody Optional<@Valid Note> optionalNote, WebRequest request) throws MethodArgumentNotValidException, BadRequestException, ResourceNotFoundException {
        if (optionalNote.isEmpty()) {
            throw new BadRequestException("Correct request should be a json Note body");
        }
        Note noteUpdated = noteService.updateNote(optionalNote.get());
        log.info("{} : {} : note = {} persisted", requestService.requestToString(request), ((ServletWebRequest) request).getHttpMethod(), noteUpdated.toString());
        return new ResponseEntity<>(noteUpdated, HttpStatus.OK);
    }

    /**
     * Deletes a note by its ID.
     *
     * @param id the ID of the note to delete
     * @param request the web request object containing request details
     * @return the HTTP status indicating the outcome of the delete operation
     */
    @DeleteMapping("/notes/{id}")
    public HttpStatus deleteNoteById(@PathVariable("id") String id, WebRequest request) {
        noteService.deleteNote(id);
        log.info("{} : {} : note = {} deleted", requestService.requestToString(request), ((ServletWebRequest) request).getHttpMethod(), id);
        return HttpStatus.OK;
    }
}