package com.medilabosolutions.type2diabetesfinder.noteservice.controller;

import com.medilabosolutions.type2diabetesfinder.noteservice.exception.RessourceNotFoundException;
import com.medilabosolutions.type2diabetesfinder.noteservice.model.Note;
import com.medilabosolutions.type2diabetesfinder.noteservice.service.NoteService;
import com.medilabosolutions.type2diabetesfinder.noteservice.service.RequestService;
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
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.context.request.WebRequest;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.tuple;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;

/**
 * Unit test class for the NoteController.
 */
@ExtendWith(MockitoExtension.class)
public class NoteControllerTest {

    @InjectMocks
    private NoteController noteController;

    @Mock
    private NoteService noteService;

    @Mock
    private RequestService requestService;

    private MockHttpServletRequest requestMock;
    private WebRequest request;
    private Note note;

    @AfterEach
    public void unsetForEachTest() {
        noteService = null;
        requestService = null;
        noteController = null;
        note = null;
    }

    @Nested
    @Tag("getNotesByPatientId")
    @DisplayName("Tests for GET /notes/patient/{patientId}")
    @TestInstance(TestInstance.Lifecycle.PER_CLASS)
    class GetNotesByPatientIdTests {

        @BeforeAll
        public void setUpForAllTests() {
            requestMock = new MockHttpServletRequest();
            requestMock.setMethod("GET");
            requestMock.setServerName("http://localhost:9003");
            requestMock.setRequestURI("/notes/patient/1");
            request = new ServletWebRequest(requestMock);
        }

        @AfterAll
        public void unSetForAllTests() {
            requestMock = null;
            request = null;
        }

        @Test
        @Tag("NoteControllerTest")
        @DisplayName("test getNotesByPatientId should return a Success ResponseEntity With List of Notes")
        public void getNotesByPatientIdTestShouldReturnASuccessResponseEntityWithListOfNotes() {
            // GIVEN
            List<Note> notes = List.of(
                    Note.builder()
                            .id("1")
                            .patientId(1)
                            .dateTime(LocalDateTime.of(2023, 5, 18, 10, 0, 0))
                            .content("Le patient déclare qu'il 'se sent très bien' Poids égal ou inférieur au poids recommandé")
                            .build(),
                    Note.builder()
                            .id("2")
                            .patientId(2)
                            .dateTime(LocalDateTime.of(2023, 5, 18, 10, 20,20))
                            .content("Le patient déclare qu'il ressent beaucoup de stress au travail Il se plaint également que son audition est anormale dernièrement")
                            .build(),
                    Note.builder()
                            .id("3")
                            .patientId(2)
                            .dateTime(LocalDateTime.of(2023, 8, 19, 10, 30,30))
                            .content("Le patient déclare avoir fait une réaction aux médicaments au cours des 3 derniers mois Il remarque également que son audition continue d'être anormale")
                            .build(),
                    Note.builder()
                            .id("4")
                            .patientId(3)
                            .dateTime(LocalDateTime.of(2023, 8, 19, 10, 50,05))
                            .content("Le patient déclare qu'il fume depuis peu")
                            .build(),
                    Note.builder()
                            .id("5")
                            .patientId(3)
                            .dateTime(LocalDateTime.of(2024, 2, 22, 11, 40,50))
                            .content("Le patient déclare qu'il est fumeur et qu'il a cessé de fumer l'année dernière Il se plaint également de crises d’apnée respiratoire anormales Tests de laboratoire indiquant un taux de cholestérol LDL élevé")
                            .build(),
                    Note.builder()
                            .id("6")
                            .patientId(4)
                            .dateTime(LocalDateTime.of(2024, 2, 22, 14, 20,00))
                            .content("Le patient déclare qu'il lui est devenu difficile de monter les escaliers Il se plaint également d’être essoufflé Tests de laboratoire indiquant que les anticorps sont élevés Réaction aux médicaments")
                            .build(),
                    Note.builder()
                            .id("7")
                            .patientId(4)
                            .dateTime(LocalDateTime.of(2024, 3, 22, 15, 20,35))
                            .content("Le patient déclare qu'il a mal au dos lorsqu'il reste assis pendant longtemps")
                            .build(),
                    Note.builder()
                            .id("8")
                            .patientId(4)
                            .dateTime(LocalDateTime.of(2024, 4, 22, 16, 21,10))
                            .content("Le patient déclare avoir commencé à fumer depuis peu Hémoglobine A1C supérieure au niveau recommandé")
                            .build(),
                    Note.builder()
                            .id("9")
                            .patientId(4)
                            .dateTime(LocalDateTime.of(2024, 5, 22, 16, 41,51))
                            .content("Taille, Poids, Cholestérol, Vertige et Réaction")
                            .build()
                    );

            when(noteService.getNotesByPatientId(anyInt())).thenReturn(notes);

            // WHEN
            ResponseEntity<List<Note>> responseEntity = noteController.getNotesByPatientId(1, request);

            // THEN
            assertThat(responseEntity.getStatusCode().is2xxSuccessful()).isTrue();
            List<Note> resultNotes = responseEntity.getBody();
            assertThat(resultNotes).isNotNull();
            assertThat(resultNotes)
                    .extracting(
                            Note::getId,
                            Note::getPatientId,
                            note -> note.getDateTime().format(DateTimeFormatter.ISO_DATE_TIME),
                            Note::getContent)
                    .containsExactly(
                            tuple("1", 1, "2023-05-18T10:00:00", "Le patient déclare qu'il 'se sent très bien' Poids égal ou inférieur au poids recommandé"),
                            tuple("2", 2, "2023-05-18T10:20:20", "Le patient déclare qu'il ressent beaucoup de stress au travail Il se plaint également que son audition est anormale dernièrement"),
                            tuple("3", 2, "2023-08-19T10:30:30", "Le patient déclare avoir fait une réaction aux médicaments au cours des 3 derniers mois Il remarque également que son audition continue d'être anormale"),
                            tuple("4", 3, "2023-08-19T10:50:05", "Le patient déclare qu'il fume depuis peu"),
                            tuple("5", 3, "2024-02-22T11:40:50", "Le patient déclare qu'il est fumeur et qu'il a cessé de fumer l'année dernière Il se plaint également de crises d’apnée respiratoire anormales Tests de laboratoire indiquant un taux de cholestérol LDL élevé"),
                            tuple("6", 4, "2024-02-22T14:20:00", "Le patient déclare qu'il lui est devenu difficile de monter les escaliers Il se plaint également d’être essoufflé Tests de laboratoire indiquant que les anticorps sont élevés Réaction aux médicaments"),
                            tuple("7", 4, "2024-03-22T15:20:35", "Le patient déclare qu'il a mal au dos lorsqu'il reste assis pendant longtemps"),
                            tuple("8", 4, "2024-04-22T16:21:10", "Le patient déclare avoir commencé à fumer depuis peu Hémoglobine A1C supérieure au niveau recommandé"),
                            tuple("9", 4, "2024-05-22T16:41:51", "Taille, Poids, Cholestérol, Vertige et Réaction")
                    );
        }
    }

    @Nested
    @Tag("getNoteById")
    @DisplayName("Tests for GET /notes/{id}")
    @TestInstance(TestInstance.Lifecycle.PER_CLASS)
    class GetNoteByIdTests {

        @BeforeAll
        public void setUpForAllTests() {
            requestMock = new MockHttpServletRequest();
            requestMock.setMethod("GET");
            requestMock.setServerName("http://localhost:9003");
            requestMock.setRequestURI("/notes/1");
            request = new ServletWebRequest(requestMock);
        }

        @AfterAll
        public void unSetForAllTests() {
            requestMock = null;
            request = null;
        }

        @Test
        @Tag("NoteControllerTest")
        @DisplayName("test getNoteById should return a Success ResponseEntity With Note")
        public void getNoteByIdTestShouldReturnASuccessResponseEntityWithNote() {
            // GIVEN
            note = Note.builder()
                    .id("1")
                    .patientId(1)
                    .dateTime(LocalDateTime.of(2023, 5, 18, 10, 0, 0))
                    .content("Le patient déclare qu'il 'se sent très bien' Poids égal ou inférieur au poids recommandé")
                    .build();

                    when(noteService.getNote(anyString())).thenReturn(note);

            // WHEN
            ResponseEntity<Note> responseEntity = noteController.getNoteById("1", request);

            // THEN
            assertThat(responseEntity.getStatusCode().is2xxSuccessful()).isTrue();
            Note resultNote = responseEntity.getBody();
            assertThat(resultNote).isNotNull();
            assertThat(resultNote)
                    .extracting(
                            Note::getId,
                            Note::getPatientId,
                            note -> note.getDateTime().format(DateTimeFormatter.ISO_DATE_TIME),
                            Note::getContent)
                    .containsExactly(
                            "1", 1, "2023-05-18T10:00:00", "Le patient déclare qu'il 'se sent très bien' Poids égal ou inférieur au poids recommandé"
                    );
        }

        @Test
        @Tag("NoteControllerTest")
        @DisplayName("test getNoteById should throw ResourceNotFoundException")
        public void getNoteByIdTestShouldThrowResourceNotFoundException() {
            // GIVEN
            when(noteService.getNote(anyString())).thenThrow(new ResourceNotFoundException("Note not found"));
            
            // WHEN
            // THEN
            assertThat(assertThrows(ResourceNotFoundException.class,
                    () -> noteController.getNoteById("1", request))
                    .getMessage()).isEqualTo("Note not found");
        }
    }

    @Nested
    @Tag("createNote")
    @DisplayName("Tests for POST /notes")
    @TestInstance(TestInstance.Lifecycle.PER_CLASS)
    class CreateNoteTests {

        @BeforeAll
        public void setUpForAllTests() {
            requestMock = new MockHttpServletRequest();
            requestMock.setMethod("POST");
            requestMock.setServerName("http://localhost:9090");
            requestMock.setRequestURI("/notes");
            request = new ServletWebRequest(requestMock);
        }

        @AfterAll
        public void unSetForAllTests() {
            requestMock = null;
            request = null;
        }

        @BeforeEach
        public void setUpForEachTest() {
            note = Note.builder()
                    .id(null)
                    .patientId(1)
                    .dateTime(LocalDateTime.of(2023, 5, 18, 10, 0, 0))
                    .content("Le patient déclare qu'il 'se sent très bien' Poids égal ou inférieur au poids recommandé")
                    .build();
        }

        @Test
        @Tag("NoteControllerTest")
        @DisplayName("test createNote should return a Success ResponseEntity With Saved Note")
        public void createNoteTestShouldReturnASuccessResponseEntityWithSavedNote() {
            // GIVEN
            Optional<Note> optionalNote = Optional.of(note);
            Note noteSaved = Note.builder()
                    .id("1")
                    .patientId(1)
                    .dateTime(LocalDateTime.of(2023, 5, 18, 10, 0, 0))
                    .content("Le patient déclare qu'il 'se sent très bien' Poids égal ou inférieur au poids recommandé")
                    .build();
            try {
                when(noteService.createNote(any(Note.class))).thenReturn(noteSaved);
            } catch (BadRequestException e) {
                throw new RuntimeException(e);
            }

            // WHEN
            ResponseEntity<Note> responseEntity =
                    assertDoesNotThrow(() -> noteController.createNote(optionalNote, request));

            // THEN
            assertThat(responseEntity.getStatusCode().value()).isEqualTo(201); // CREATED
            Note resultNote = responseEntity.getBody();
            assertThat(resultNote).isNotNull();
            assertThat(resultNote)
                    .extracting(
                            Note::getId,
                            Note::getPatientId,
                            note -> note.getDateTime().format(DateTimeFormatter.ISO_DATE_TIME),
                            Note::getContent)
                    .containsExactly(
                            "1", 1, "2023-05-18T10:00:00", "Le patient déclare qu'il 'se sent très bien' Poids égal ou inférieur au poids recommandé"
                    );
        }

        @Test
        @Tag("NoteControllerTest")
        @DisplayName("test createNote should throw BadRequestException")
        public void createNoteTestShouldThrowBadRequestException() {
            // GIVEN
            Optional<Note> optionalNote = Optional.empty();

            // WHEN
            // THEN
            assertThat(assertThrows(BadRequestException.class,
                    () -> noteController.createNote(optionalNote, request))
                    .getMessage()).isEqualTo("Correct request should be a json Note body");
        }
    }

    @Nested
    @Tag("updateNote")
    @DisplayName("Tests for PUT /notes")
    @TestInstance(TestInstance.Lifecycle.PER_CLASS)
    class UpdateNoteTests {

        @BeforeAll
        public void setUpForAllTests() {
            requestMock = new MockHttpServletRequest();
            requestMock.setMethod("PUT");
            requestMock.setServerName("http://localhost:9003");
            requestMock.setRequestURI("/notes");
            request = new ServletWebRequest(requestMock);
        }

        @AfterAll
        public void unSetForAllTests() {
            requestMock = null;
            request = null;
        }

        @BeforeEach
        public void setUpForEachTest() {
            note = Note.builder()
                    .id("1")
                    .patientId(1)
                    .dateTime(LocalDateTime.of(2023, 5, 18, 10, 0, 0))
                    .content("Le patient déclare qu'il 'se sent très bien' Poids égal ou inférieur au poids recommandé")
                    .build();
        }

        @Test
        @Tag("NoteControllerTest")
        @DisplayName("test updateNote should return a Success ResponseEntity With Updated Note")
        public void updateNoteTestShouldReturnASuccessResponseEntityWithUpdatedNote() {
            // GIVEN
            Optional<Note> optionalNote = Optional.of(note);
            Note noteUpdated = Note.builder()
                    .id("1")
                    .patientId(1)
                    .dateTime(LocalDateTime.of(2023, 5, 18, 10, 0, 0))
                    .content("MàJ : Le patient déclare qu'il s'était trompé et qu'il ne se sentait pas très bien' Poids égal ou inférieur au poids recommandé")
                    .build();
            when(noteService.updateNote(any(Note.class))).thenReturn(noteUpdated);

            // WHEN
            ResponseEntity<Note> responseEntity =
                    assertDoesNotThrow(() -> noteController.updateNote(optionalNote, request));

            // THEN
            assertThat(responseEntity.getStatusCode().is2xxSuccessful()).isTrue();
            Note resultNote = responseEntity.getBody();
            assertThat(resultNote).isNotNull();
            assertThat(resultNote)
                    .extracting(
                            Note::getId,
                            Note::getPatientId,
                            note -> note.getDateTime().format(DateTimeFormatter.ISO_DATE_TIME),
                            Note::getContent)
                    .containsExactly(
                            "1", 1, "2023-05-18T10:00:00", "MàJ : Le patient déclare qu'il s'était trompé et qu'il ne se sentait pas très bien' Poids égal ou inférieur au poids recommandé"
                    );
        }

        @Test
        @Tag("NoteControllerTest")
        @DisplayName("test updateNote should throw BadRequestException")
        public void updateNoteTestShouldThrowBadRequestException() {
            // GIVEN
            Optional<Note> optionalNote = Optional.empty();

            // WHEN
            // THEN
            assertThat(assertThrows(BadRequestException.class,
                    () -> noteController.updateNote(optionalNote, request))
                    .getMessage()).isEqualTo("Correct request should be a json Note body");
        }

        @Test
        @Tag("NoteControllerTest")
        @DisplayName("test updateNote should throw ResourceNotFoundException")
        public void updateNoteTestShouldThrowResourceNotFoundException() {
            // GIVEN
            Optional<Note> optionalNote = Optional.of(note);
            when(noteService.updateNote(any(Note.class))).thenThrow(new ResourceNotFoundException("Note not found with id: 1"));

            // WHEN
            // THEN
            assertThat(assertThrows(ResourceNotFoundException.class,
                    () -> noteController.updateNote(optionalNote, request))
                    .getMessage()).isEqualTo("Note not found with id: 1");
        }
    }

    @Nested
    @Tag("deleteNoteById")
    @DisplayName("Tests for DELETE /notes/{id}")
    @TestInstance(TestInstance.Lifecycle.PER_CLASS)
    class DeleteNoteByIdTests {

        @BeforeAll
        public void setUpForAllTests() {
            requestMock = new MockHttpServletRequest();
            requestMock.setMethod("DELETE");
            requestMock.setServerName("http://localhost:9003");
            requestMock.setRequestURI("/notes/1");
            request = new ServletWebRequest(requestMock);
        }

        @AfterAll
        public void unSetForAllTests() {
            requestMock = null;
            request = null;
        }

        @Test
        @Tag("NoteControllerTest")
        @DisplayName("test deleteNoteById should return HttpStatus.OK")
        public void deleteNoteByIdTestShouldReturnHttpStatusOK() {
            // GIVEN
            // WHEN
            HttpStatus httpStatus = noteController.deleteNoteById("1", request);

            // THEN
            assertThat(httpStatus.is2xxSuccessful()).isTrue();
        }
    }
}