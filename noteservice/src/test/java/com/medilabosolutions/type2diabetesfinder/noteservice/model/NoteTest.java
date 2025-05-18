package com.medilabosolutions.type2diabetesfinder.noteservice.model;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

import java.time.LocalDateTime;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Unit test class for the Note model.
 */
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class NoteTest {

    private ValidatorFactory factory;
    private Validator validator;
    private Note note;

    @BeforeAll
    public void setUpForAllTests() {
        factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @AfterAll
    public void undefForAllTests() {
        validator = null;
        factory = null;
    }

    @BeforeEach
    public void setUpPerTest() {
        note = new Note();
    }

    @AfterEach
    public void undefPerTest() {
        note = null;
    }

    @ParameterizedTest(name = "{0} should be valid")
    @ValueSource(strings = {"This is a valid note content", "Short note"})
    @Tag("NoteTest")
    @DisplayName("@Valid test with valid note content should be valid")
    public void testValidContentShouldBeValid(String content) {
        // GIVEN
        note = Note.builder()
                .id("1")
                .patientId(1)
                .dateTime(LocalDateTime.now().minusDays(1))
                .content(content)
                .build();

        // WHEN
        Set<ConstraintViolation<Note>> constraintViolations = validator.validate(note);

        // THEN
        assertThat(constraintViolations).isEmpty();
    }

    @ParameterizedTest(name = "{0} should throw a ConstraintViolationException")
    @NullAndEmptySource
    @ValueSource(strings = {"   "})
    @Tag("NoteTest")
    @DisplayName("@Valid test with invalid note content should throw a ConstraintViolationException")
    public void testInvalidContentShouldThrowConstraintViolationException(String content) {
        // GIVEN
        note = Note.builder()
                .id("1")
                .patientId(1)
                .dateTime(LocalDateTime.now().minusDays(1))
                .content(content)
                .build();

        // WHEN
        Set<ConstraintViolation<Note>> constraintViolations = validator.validate(note);

        // THEN
        assertThat(constraintViolations).isNotEmpty();
        assertThat(constraintViolations.iterator().next().getMessage()).isEqualTo("Content is mandatory");
    }

    @Test
    @Tag("NoteTest")
    @DisplayName("@Valid test with null patientId should throw a ConstraintViolationException")
    public void testNullPatientIdShouldThrowConstraintViolationException() {
        // GIVEN
        note = Note.builder()
                .id("1")
                .patientId(null)
                .dateTime(LocalDateTime.now().minusDays(1))
                .content("Valid content")
                .build();

        // WHEN
        Set<ConstraintViolation<Note>> constraintViolations = validator.validate(note);

        // THEN
        assertThat(constraintViolations).isNotEmpty();
        assertThat(constraintViolations.iterator().next().getMessage()).isEqualTo("Patient ID is mandatory");
    }

    @Test
    @Tag("NoteTest")
    @DisplayName("@Valid test with null dateTime should throw a ConstraintViolationException")
    public void testNullDateTimeShouldThrowConstraintViolationException() {
        // GIVEN
        note = Note.builder()
                .id("1")
                .patientId(1)
                .dateTime(null)
                .content("Valid content")
                .build();

        // WHEN
        Set<ConstraintViolation<Note>> constraintViolations = validator.validate(note);

        // THEN
        assertThat(constraintViolations).isNotEmpty();
        assertThat(constraintViolations.iterator().next().getMessage()).isEqualTo("Date is mandatory");
    }
}