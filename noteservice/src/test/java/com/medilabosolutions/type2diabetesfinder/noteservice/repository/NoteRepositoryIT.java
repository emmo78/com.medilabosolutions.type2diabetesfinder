package com.medilabosolutions.type2diabetesfinder.noteservice.repository;

import com.medilabosolutions.type2diabetesfinder.noteservice.model.Note;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.InvalidDataAccessApiUsageException;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.tuple;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * Integration test class for the NoteRepository.
 * Tests MongoDB operations and constraints.
 */
@SpringBootTest
@ActiveProfiles("test")
public class NoteRepositoryIT {

    @Autowired
    private NoteRepository noteRepository;

    private Note note;

    @BeforeEach
    public void setUpPerTest() {
        note = new Note();
    }

    @AfterEach
    public void undefPerTest() {
        noteRepository.deleteAll();
        note = null;
    }

    @Nested
    @Tag("saveNoteTests")
    @DisplayName("Tests for saving notes")
    class SaveNoteTests {

        @Test
        @Tag("NoteRepositoryIT")
        @DisplayName("save test new note should persist note with new id")
        public void saveTestShouldPersistNoteWithNewId() {
            // GIVEN
            note = Note.builder()
                    .id(null)
                    .patientId(1)
                    .dateTime(LocalDateTime.of(2023, 5, 18, 10, 0, 0))
                    .content("Le patient déclare qu'il 'se sent très bien' Poids égal ou inférieur au poids recommandé")
                    .build();

            // WHEN
            Note noteResult = noteRepository.save(note);

            // THEN
            Optional<String> idOpt = Optional.ofNullable(noteResult.getId());
            assertThat(idOpt).isPresent();
            idOpt.ifPresent(id -> assertThat(noteRepository.findById(id)).get().extracting(
                    Note::getPatientId,
                    note -> note.getDateTime().format(DateTimeFormatter.ISO_DATE_TIME),
                    Note::getContent
                    )
                    .containsExactly(1
                            , "2023-05-18T10:00:00"
                            , "Le patient déclare qu'il 'se sent très bien' Poids égal ou inférieur au poids recommandé"));
        }

        @Test
        @Tag("NoteRepositoryIT")
        @DisplayName("save test update note")
        public void saveTestUpdateNoteShouldPersistIt() {
            // GIVEN
            note = Note.builder()
                    .id(null)
                    .patientId(1)
                    .dateTime(LocalDateTime.of(2023, 5, 18, 10, 0, 0))
                    .content("Le patient déclare qu'il 'se sent très bien' Poids égal ou inférieur au poids recommandé")
                    .build();

            String id = noteRepository.save(note).getId();

            Note noteUpdated = Note.builder()
                    .id(id)
                    .patientId(1)
                    .dateTime(LocalDateTime.of(2023, 5, 18, 10, 0, 0))
                    .content("MàJ : Le patient déclare qu'il s'était trompé et qu'il ne se sentait pas très bien' Poids égal ou inférieur au poids recommandé")
                    .build();

            // WHEN
            // THEN
            assertThat(assertDoesNotThrow(() -> noteRepository.save(noteUpdated)))
                    .extracting(
                            Note::getId,
                            Note::getPatientId,
                            note -> note.getDateTime().format(DateTimeFormatter.ISO_DATE_TIME),
                            Note::getContent)
                    .containsExactly(
                            id
                            , 1
                            , "2023-05-18T10:00:00", "MàJ : Le patient déclare qu'il s'était trompé et qu'il ne se sentait pas très bien' Poids égal ou inférieur au poids recommandé");

        }

        @Test
        @Tag("NoteRepositoryIT")
        @DisplayName("save test null should throw IllegalArgumentExceptionException")
        public void saveTestNullShouldThrowAnIllegalArgumentExceptionException() {
            // GIVEN
            // WHEN
            // THEN
            assertThat(assertThrows(IllegalArgumentException.class, () -> noteRepository.save(null)).getMessage())
                    .contains("Entity must not be null");
        }
    }

    @Test
    @Tag("NoteRepositoryIT")
    @DisplayName("find by Id Test with id null should throw an IllegalArgumentException")
    public void findByIdTestWithIdNullShouldThrowAnIllegalArgumentException() {
        // GIVEN
        // WHEN
        // THEN
        assertThat(assertThrows(IllegalArgumentException.class, () -> noteRepository.findById(null)).getMessage())
                .contains("The given id must not be null");
    }

    @Test
    @Tag("NoteRepositoryIT")
    @DisplayName("deleteById test null should throw IllegalArgumentException")
    public void deleteByIdTestWithIdNullShouldThrowAnIllegalArgumentException() {
        // GIVEN
        String id = null;
        // WHEN
        // THEN
        assertThat(assertThrows(IllegalArgumentException.class, () -> noteRepository.deleteById(id)).getMessage())
                .contains("The given id must not be null");
    }

    @Test
    @Tag("NoteRepositoryIT")
    @DisplayName("existById test null should throw IllegalArgumentException")
    public void existByIdTestNull() {
        // GIVEN
        // WHEN
        // THEN
        assertThat(assertThrows(IllegalArgumentException.class, () -> noteRepository.existsById(null)).getMessage())
                .contains("The given id must not be null");
    }

    @Test
    @Tag("NoteRepositoryIT")
    @DisplayName("findAllByPatientIdOrderByDateTimeDesc test should return notes ordered by date time desc")
    public void findAllByPatientIdOrderByDateTimeDescTestShouldReturnNotesDescOrderedByDateTimeDesc() {
        // GIVEN
        Note note1 = Note.builder()
                .id(null)
                .patientId(1)
                .dateTime(LocalDateTime.of(2023, 5, 18, 10, 0, 0))
                .content("Le patient déclare qu'il 'se sent très bien' Poids égal ou inférieur au poids recommandé")
                .build();
        Note note2 = Note.builder()
                .id(null)
                .patientId(2)
                .dateTime(LocalDateTime.of(2023, 5, 18, 10, 20,20))
                .content("Le patient déclare qu'il ressent beaucoup de stress au travail Il se plaint également que son audition est anormale dernièrement")
                .build();
        Note note3 = Note.builder()
                .id(null)
                .patientId(2)
                .dateTime(LocalDateTime.of(2023, 8, 19, 10, 30,30))
                .content("Le patient déclare avoir fait une réaction aux médicaments au cours des 3 derniers mois Il remarque également que son audition continue d'être anormale")
                .build();
        Note note4 = Note.builder()
                .id(null)
                .patientId(3)
                .dateTime(LocalDateTime.of(2023, 8, 19, 10, 50,05))
                .content("Le patient déclare qu'il fume depuis peu")
                .build();
        Note note5 = Note.builder()
                .id(null)
                .patientId(3)
                .dateTime(LocalDateTime.of(2024, 2, 22, 11, 40,50))
                .content("Le patient déclare qu'il est fumeur et qu'il a cessé de fumer l'année dernière Il se plaint également de crises d’apnée respiratoire anormales Tests de laboratoire indiquant un taux de cholestérol LDL élevé")
                .build();
        Note note6 = Note.builder()
                .id(null)
                .patientId(4)
                .dateTime(LocalDateTime.of(2024, 2, 22, 14, 20,00))
                .content("Le patient déclare qu'il lui est devenu difficile de monter les escaliers Il se plaint également d’être essoufflé Tests de laboratoire indiquant que les anticorps sont élevés Réaction aux médicaments")
                .build();
        Note note7 = Note.builder()
                .id(null)
                .patientId(4)
                .dateTime(LocalDateTime.of(2024, 3, 22, 15, 20,35))
                .content("Le patient déclare qu'il a mal au dos lorsqu'il reste assis pendant longtemps")
                .build();
        Note note8 = Note.builder()
                .id(null)
                .patientId(4)
                .dateTime(LocalDateTime.of(2024, 4, 22, 16, 21,10))
                .content("Le patient déclare avoir commencé à fumer depuis peu Hémoglobine A1C supérieure au niveau recommandé")
                .build();
        Note note9 = Note.builder()
                .id(null)
                .patientId(4)
                .dateTime(LocalDateTime.of(2024, 5, 22, 16, 41,51))
                .content("Taille, Poids, Cholestérol, Vertige et Réaction")
                .build();

        noteRepository.save(note1);
        noteRepository.save(note2);
        noteRepository.save(note3);
        noteRepository.save(note4);
        noteRepository.save(note5);
        String id6 = noteRepository.save(note6).getId();
        String id7 = noteRepository.save(note7).getId();
        String id8 = noteRepository.save(note8).getId();
        String id9 = noteRepository.save(note9).getId();

        // WHEN
        List<Note> notes = noteRepository.findAllByPatientIdOrderByDateTimeDesc(4);

        // THEN
        assertThat(notes).hasSize(4);
        assertThat(notes)
                .extracting(
                        Note::getId,
                        Note::getPatientId,
                        note -> note.getDateTime().format(DateTimeFormatter.ISO_DATE_TIME),
                        Note::getContent)
                .containsExactly(
                        tuple(id9, 4, "2024-05-22T16:41:51", "Taille, Poids, Cholestérol, Vertige et Réaction")
                        ,tuple(id8, 4, "2024-04-22T16:21:10", "Le patient déclare avoir commencé à fumer depuis peu Hémoglobine A1C supérieure au niveau recommandé")
                        ,tuple(id7, 4, "2024-03-22T15:20:35", "Le patient déclare qu'il a mal au dos lorsqu'il reste assis pendant longtemps")
                        ,tuple(id6, 4, "2024-02-22T14:20:00", "Le patient déclare qu'il lui est devenu difficile de monter les escaliers Il se plaint également d’être essoufflé Tests de laboratoire indiquant que les anticorps sont élevés Réaction aux médicaments")
                );
    }

    @Test
    @Tag("NoteRepositoryIT")
    @DisplayName("findAllByPatientIdOrderByDateTimeDesc test with non-existent patient ID should return empty list")
    public void findAllByPatientIdOrderByDateTimeDescTestWithNonExistentPatientIdShouldReturnEmptyList() {
        // GIVEN
        // WHEN
        List<Note> notes = noteRepository.findAllByPatientIdOrderByDateTimeDesc(999);

        // THEN
        assertThat(notes).isEmpty();
    }
}