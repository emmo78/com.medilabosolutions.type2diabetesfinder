package com.medilabosolutions.type2diabetesfinder.noteservice.repository;

import com.medilabosolutions.type2diabetesfinder.noteservice.model.Note;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

/**
 * NoteRepository is a Data Access Object (DAO) interface for managing Note entities.
 * It extends MongoRepository to provide basic CRUD operations for MongoDB.
 */
public interface NoteRepository extends MongoRepository<Note, String> {
    
    /**
     * Find all notes by patient ID ordered by date time in descending order.
     *
     * @param patientId the ID of the patient
     * @return a list of notes for the specified patient, ordered by date time in descending order
     */
    List<Note> findAllByPatientIdOrderByDateTimeDesc(Integer patientId);
}