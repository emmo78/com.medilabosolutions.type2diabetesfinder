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

import java.util.List;

/**
 * Implementation of the NoteService interface for managing note entities.
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class NoteServiceImpl implements NoteService {

    private final NoteRepository noteRepository;

    /**
     * {@inheritDoc}
     */
    @Override
    public List<Note> getNotesByPatientId(Integer patientId) {
        return noteRepository.findAllByPatientIdOrderByDateTimeDesc(patientId);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Note getNote(String id) throws RessourceNotFoundException {
        return noteRepository.findById(id)
                .orElseThrow(() -> new RessourceNotFoundException("Note non trouvée avec l'identifiant : " + id));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Note createNote(Note note) throws BadRequestException {
        if (note.getId() != null) {
            throw new BadRequestException("A new note cannot already have an ID");
        }
        return noteRepository.save(note);
    }

    /**
     * {@inheritDoc}
     */
    @Override
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
     * {@inheritDoc}
     */
    @Override
    public void deleteNote(String id) {
        noteRepository.deleteById(id);
    }
}