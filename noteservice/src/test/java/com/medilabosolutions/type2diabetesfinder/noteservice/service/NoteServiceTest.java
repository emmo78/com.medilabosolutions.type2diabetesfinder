package com.medilabosolutions.type2diabetesfinder.noteservice.service;

import com.medilabosolutions.type2diabetesfinder.noteservice.exception.RessourceNotFoundException;
import com.medilabosolutions.type2diabetesfinder.noteservice.model.Note;
import com.medilabosolutions.type2diabetesfinder.noteservice.repository.NoteRepository;
import com.medilabosolutions.type2diabetesfinder.noteservice.service.NoteServiceImpl;
import org.apache.coyote.BadRequestException;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.rest.webmvc.ResourceNotFoundException;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.tuple;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

/**
 * Unit test class for the NoteService.
 */
@ExtendWith(MockitoExtension.class)
public class NoteServiceTest {

    @InjectMocks
    private NoteServiceImpl noteService;

    @Mock
    private NoteRepository noteRepository;

    private Note note;

    @Nested
    @Tag("getNotesByPatientIdTests")
    @DisplayName("Tests for getting notes by patient ID")
    class GetNotesByPatientIdTests {

        @AfterEach
        public void unSetForEachTests() {
            noteService = null;
            note = null;
        }

        @Test
        @Tag("NoteServiceTest")
        @DisplayName("test getNotesByPatientId should return expected notes")
        public void getNotesByPatientIdTestShouldReturnExpectedNotes() {
            // GIVEN
            List<Note> givenNotes = List.of(
                    Note.builder()
                            .id("3")
                            .patientId(2)
                            .dateTime(LocalDateTime.of(2023, 8, 19, 10, 30,30))
                            .content("Le patient déclare avoir fait une réaction aux médicaments au cours des 3 derniers mois Il remarque également que son audition continue d'être anormale")
                            .build(),
                    Note.builder()
                            .id("2")
                            .patientId(2)
                            .dateTime(LocalDateTime.of(2023, 5, 18, 10, 20,20))
                            .content("Le patient déclare qu'il ressent beaucoup de stress au travail Il se plaint également que son audition est anormale dernièrement")
                            .build());
            when(noteRepository.findAllByPatientIdOrderByDateTimeDesc(any(Integer.class))).thenReturn(givenNotes);

            // WHEN
            List<Note> resultedNotes = noteService.getNotesByPatientId(2);

            // THEN
            assertThat(resultedNotes)
                    .extracting(
                            Note::getId,
                            Note::getPatientId,
                            note -> note.getDateTime().format(DateTimeFormatter.ISO_DATE_TIME),
                            Note::getContent)
                    .containsExactly(
                            tuple("3", 2, "2023-08-19T10:30:30", "Le patient déclare avoir fait une réaction aux médicaments au cours des 3 derniers mois Il remarque également que son audition continue d'être anormale"),
                            tuple("2", 2, "2023-05-18T10:20:20", "Le patient déclare qu'il ressent beaucoup de stress au travail Il se plaint également que son audition est anormale dernièrement"));
        }
    }

    @Nested
    @Tag("getNoteTests")
    @DisplayName("Tests for getting note")
    class GetNoteTests {

        @AfterEach
        public void unSetForEachTests() {
            noteService = null;
            note = null;
        }

        @Test
        @Tag("NoteServiceTest")
        @DisplayName("test getNote should return expected note")
        public void getNoteTestShouldReturnExpectedNote() {
            // GIVEN
            note = Note.builder()
                    .id("1")
                    .patientId(1)
                    .dateTime(LocalDateTime.of(2023, 5, 18, 10, 0, 0))
                    .content("Le patient déclare qu'il 'se sent très bien' Poids égal ou inférieur au poids recommandé")
                    .build();
            when(noteRepository.findById(anyString())).thenReturn(Optional.of(note));

            // WHEN
            Note noteResult = noteService.getNote("1");

            // THEN
            assertThat(noteResult).extracting(
                    Note::getId,
                    Note::getPatientId,
                    note -> note.getDateTime().format(DateTimeFormatter.ISO_DATE_TIME),
                    Note::getContent)
                    .containsExactly(
                            "1",
                            1,
                            "2023-05-18T10:00:00",
                            "Le patient déclare qu'il 'se sent très bien' Poids égal ou inférieur au poids recommandé");
        }

        @Test
        @Tag("NoteServiceTest")
        @DisplayName("test getNote should throw RessourceNotFoundException")
        public void getNoteTestShouldThrowsRessourceNotFoundException() {
            // GIVEN
            when(noteRepository.findById(anyString())).thenReturn(Optional.empty());

            // WHEN
            // THEN
            assertThat(assertThrows(RessourceNotFoundException.class,
                    () -> noteService.getNote("1"))
                    .getMessage()).isEqualTo("Note non trouvée avec l'identifiant : 1");
        }
    }

    @Nested
    @Tag("createNoteTests")
    @DisplayName("Tests for create note")
    class CreateNoteTests {

        @BeforeEach
        public void setUpForEachTest() {
            note = Note.builder()
                    .id(null)
                    .patientId(1)
                    .dateTime(LocalDateTime.of(2023, 5, 18, 10, 0, 0))
                    .content("Le patient déclare qu'il 'se sent très bien' Poids égal ou inférieur au poids recommandé")
                    .build();
        }

        @AfterEach
        public void unSetForEachTests() {
            noteService = null;
            note = null;
        }

        @Test
        @Tag("NoteServiceTest")
        @DisplayName("test createNote should persist and return note")
        public void createNoteTestShouldPersistAndReturnNote() {
            // GIVEN
            Note noteExpected = Note.builder()
                    .id("1")
                    .patientId(1)
                    .dateTime(LocalDateTime.of(2023, 5, 18, 10, 0, 0))
                    .content("Le patient déclare qu'il 'se sent très bien' Poids égal ou inférieur au poids recommandé")
                    .build();
            when(noteRepository.save(any(Note.class))).thenReturn(noteExpected);

            // WHEN
            Note resultedNote = assertDoesNotThrow(() -> noteService.createNote(note));

            // THEN
            assertThat(resultedNote).extracting(
                    Note::getId,
                    Note::getPatientId,
                    note -> note.getDateTime().format(DateTimeFormatter.ISO_DATE_TIME),
                    Note::getContent)
                .containsExactly(
                    "1",
                    1,
                    "2023-05-18T10:00:00",
                    "Le patient déclare qu'il 'se sent très bien' Poids égal ou inférieur au poids recommandé");
        }

        @Test
        @Tag("NoteServiceTest")
        @DisplayName("test createNote should throw BadRequestException on Not Null Id")
        public void createNoteTestShouldThrowsBadRequestExceptionOnNotNullId() {
            // GIVEN
            note.setId("1");

            // WHEN
            // THEN
            assertThat(assertThrows(BadRequestException.class,
                    () -> noteService.createNote(note))
                    .getMessage()).isEqualTo("A new note cannot already have an ID");
        }
    }

    @Nested
    @Tag("updateNoteTests")
    @DisplayName("Tests for update note")
    class UpdateNoteTests {

        @BeforeEach
        public void setUpForEachTest() {
            note = Note.builder()
                    .id("1")
                    .patientId(1)
                    .dateTime(LocalDateTime.of(2023, 5, 18, 10, 0, 0))
                    .content("Le patient déclare qu'il 'se sent très bien' Poids égal ou inférieur au poids recommandé")
                    .build();
        }

        @AfterEach
        public void unSetForEachTests() {
            noteService = null;
            note = null;
        }

        @Test
        @Tag("NoteServiceTest")
        @DisplayName("test updateNote should persist and return note")
        public void updateNoteTestShouldPersistAndReturnNote() {
            // GIVEN
            Note noteExpected = Note.builder()
                    .id("1")
                    .patientId(1)
                    .dateTime(LocalDateTime.of(2023, 5, 18, 10, 0, 0))
                    .content("MàJ : Le patient déclare qu'il s'était trompé et qu'il ne se sentait pas très bien' Poids égal ou inférieur au poids recommandé")
                    .build();
            when(noteRepository.existsById(anyString())).thenReturn(true);
            when(noteRepository.save(any(Note.class))).thenReturn(noteExpected);

            // WHEN
            Note resultedNote = noteService.updateNote(note);

            // THEN
            assertThat(resultedNote).extracting(
                    Note::getId,
                    Note::getPatientId,
                    note -> note.getDateTime().format(DateTimeFormatter.ISO_DATE_TIME),
                    Note::getContent)
                    .containsExactly(
                            "1",
                            1,
                            "2023-05-18T10:00:00",
                            "MàJ : Le patient déclare qu'il s'était trompé et qu'il ne se sentait pas très bien' Poids égal ou inférieur au poids recommandé");
        }

        @Test
        @Tag("NoteServiceTest")
        @DisplayName("test updateNote should throw ResourceNotFoundException")
        public void updateNoteTestShouldThrowsResourceNotFoundException() {
            // GIVEN
            when(noteRepository.existsById(anyString())).thenReturn(false);

            // WHEN
            // THEN
            assertThat(assertThrows(ResourceNotFoundException.class,
                    () -> noteService.updateNote(note))
                    .getMessage()).isEqualTo("Note not found with id: 1");
        }

        @Test
        @Tag("NoteServiceTest")
        @DisplayName("test updateNote should throw IllegalArgumentException on Null Id")
        public void updateNoteTestShouldThrowsIllegalArgumentExceptionOnNullId() {
            // GIVEN
            note.setId(null);

            // WHEN
            // THEN
            assertThat(assertThrows(IllegalArgumentException.class,
                    () -> noteService.updateNote(note))
                    .getMessage()).isEqualTo("ID cannot be null for update operation");
        }
    }

    @Nested
    @Tag("deleteNoteTests")
    @DisplayName("Tests for deleting Note")
    class DeleteNoteTests {

        @BeforeEach
        public void setUpForEachTest() {
            note = Note.builder()
                    .id("1")
                    .patientId(1)
                    .dateTime(LocalDateTime.of(2023, 1, 1, 10, 0))
                    .content("Patient shows signs of reaction to medication")
                    .build();
        }

        @AfterEach
        public void unSetForEachTests() {
            noteService = null;
            note = null;
        }

        @Test
        @Tag("NoteServiceTest")
        @DisplayName("test deleteNote by Id should delete it")
        public void deleteNoteByIdTestShouldDeleteIt() {
            // GIVEN
            // WHEN
            // THEN
            assertDoesNotThrow(() -> noteService.deleteNote("1"));
        }
    }
}
