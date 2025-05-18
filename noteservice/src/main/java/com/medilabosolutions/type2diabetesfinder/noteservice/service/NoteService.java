package com.medilabosolutions.type2diabetesfinder.noteservice.service;

import com.medilabosolutions.type2diabetesfinder.noteservice.exception.RessourceNotFoundException;
import com.medilabosolutions.type2diabetesfinder.noteservice.model.Note;
import org.apache.coyote.BadRequestException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.rest.webmvc.ResourceNotFoundException;

import java.util.List;

/**
 * NoteService is an interface that defines the operations for managing note entities.
 */
public interface NoteService {

    /**
     * Retrieves all notes for a specific patient, ordered by date time in descending order.
     *
     * @param patientId the ID of the patient
     * @return a list of notes for the specified patient
     */
    List<Note> getNotesByPatientId(Integer patientId);

    /**
     * Récupère une note par son identifiant.
     *
     * @param id l'identifiant de la note à récupérer
     * @return la note avec l'identifiant spécifié
     */
    Note getNote(String id) throws RessourceNotFoundException;

    /**
     * Creates a new note.
     *
     * @param note the note to create
     * @return the created note
     * @throws BadRequestException if the note has an ID
     */
    Note createNote(Note note) throws BadRequestException;

    /**
     * Updates an existing note.
     *
     * @param note the note to update
     * @return the updated note
     * @throws ResourceNotFoundException if the note does not exist
     */
    Note updateNote(Note note) throws ResourceNotFoundException;

    /**
     * Deletes a note by its ID.
     *
     * @param id the ID of the note to delete
     */
    void deleteNote(String id);
}