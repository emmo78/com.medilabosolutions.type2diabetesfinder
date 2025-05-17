package com.medilabosolutions.type2diabetesfinder.noteservice.service;

import com.medilabosolutions.type2diabetesfinder.noteservice.exception.RessourceNotFoundException;
import com.medilabosolutions.type2diabetesfinder.noteservice.model.Note;
import com.medilabosolutions.type2diabetesfinder.noteservice.repository.NoteRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.coyote.BadRequestException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.rest.webmvc.ResourceNotFoundException;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Service class for handling Note-related business logic.
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class NoteService {

    private final NoteRepository noteRepository;

    /**
     * Récupère une note par son identifiant.
     *
     * @param id l'identifiant de la note à récupérer
     * @return la note avec l'identifiant spécifié
     * @throws RessourceNotFoundException si aucune note n'est trouvée avec l'identifiant fourni
     */
    public Note getNote(String id) throws RessourceNotFoundException {
        return noteRepository.findById(id)
                .orElseThrow(() -> new RessourceNotFoundException("Note non trouvée avec l'identifiant : " + id));
    }

    /**
     * Retrieves all notes with pagination.
     *
     * @param pageable pagination information
     * @return a page of notes
     */
    public Page<Note> getNotes(Pageable pageable) {
        return noteRepository.findAll(pageable);
    }

    /**
     * Retrieves all notes for a specific patient, ordered by date time in descending order.
     *
     * @param patientId the ID of the patient
     * @return a list of notes for the specified patient
     */
    public List<Note> getNotesByPatientId(Integer patientId) {
        return noteRepository.findAllByPatientIdOrderByDateTimeDesc(patientId);
    }

    /**
     * Creates a new note.
     *
     * @param note the note to create
     * @return the created note
     * @throws BadRequestException if the note has an ID
     */
    public Note createNote(Note note) throws BadRequestException {
        if (note.getId() != null) {
            throw new BadRequestException("A new note cannot already have an ID");
        }
        return noteRepository.save(note);
    }

    /**
     * Updates an existing note.
     *
     * @param note the note to update
     * @return the updated note
     * @throws ResourceNotFoundException if the note does not exist
     */
    public Note updateNote(Note note) {
        if (note.getId() == null) {
            throw new IllegalArgumentException("ID cannot be null for update operation");
        }
        // Check if the note exists
        if (!noteRepository.existsById(note.getId())) {
            throw new ResourceNotFoundException("Note not found with id: " + note.getId());
        }
        return noteRepository.save(note);
    }

    /**
     * Deletes a note by its ID.
     *
     * @param id the ID of the note to delete
     */
    public void deleteNote(String id) {
        noteRepository.deleteById(id);
    }
}