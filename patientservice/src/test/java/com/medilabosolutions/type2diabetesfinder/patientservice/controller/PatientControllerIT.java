package com.medilabosolutions.type2diabetesfinder.patientservice.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.medilabosolutions.type2diabetesfinder.patientservice.model.Patient;
import com.medilabosolutions.type2diabetesfinder.patientservice.repository.PatientRepository;
import jakarta.inject.Inject;
import lombok.SneakyThrows;
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.rest.webmvc.ResourceNotFoundException;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.params.provider.Arguments.arguments;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("mytest")
@Disabled
public class PatientControllerIT {

    @Inject
    private MockMvc mvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private PatientRepository patientRepository;

    @BeforeEach
    public void setUpPerTest() {
        patientRepository.deleteAll();
        patientRepository.flush();
    }

    @SneakyThrows
    @Test
    @Tag("PatientControllerIT")
    @DisplayName("getPatients IT should return patients")
    public void getPatientsITShouldReturnPatients() {

        //GIVEN
        List<Patient> givenPatients = List.of(
                Patient.builder()
                        .firstName("Test")
                        .lastName("TestNone")
                        .birthDate(LocalDate.of(1966, 12, 31))
                        .genre("F")
                        .address("1 Brookside St")
                        .phoneNumber("100-222-3333")
                        .build(),
                Patient.builder()
                        .firstName("Test")
                        .lastName("TestBorderline")
                        .birthDate(LocalDate.of(1945, 06, 24))
                        .genre("M")
                        .address("2 High St")
                        .phoneNumber("200-333-4444")
                        .build(),
                Patient.builder()
                        .firstName("Test")
                        .lastName("TestDanger")
                        .birthDate(LocalDate.of(2004, 06, 18))
                        .genre("M")
                        .address("3 Club Road")
                        .phoneNumber("300-444-5555")
                        .build(),
                Patient.builder()
                        .firstName("Test")
                        .lastName("TestEarlyOnset")
                        .birthDate(LocalDate.of(2002, 06, 28))
                        .genre("F")
                        .address("4 Valley Dr")
                        .phoneNumber("400-555-6666")
                        .build()
        );
        givenPatients.forEach(patient -> patient.setId(patientRepository.saveAndFlush(patient).getId()));
        String givenPatientsString = objectMapper.writeValueAsString(givenPatients.subList(0, 3));

        //WHEN
        final MvcResult result = mvc.perform(get("/patients?pageNumber=0"))
                .andReturn();

        //THEN
        assertThat(result.getResponse().getStatus()).isEqualTo(200);
        assertThat(result.getResponse().getContentAsString()).contains(givenPatientsString);
    }

    @Nested
    @Tag("getPatientById")
    @DisplayName("IT for GET /patients/{id}")
    class getPatientByIdIT {

        Patient givenPatient;
        String givenPatientString;
        int id;

        @SneakyThrows
        @BeforeEach
        public void setUpPerTest() {
            givenPatient = Patient.builder()
                    .firstName("Test")
                    .lastName("TestNone")
                    .birthDate(LocalDate.of(1966, 12, 31))
                    .genre("F")
                    .address("1 Brookside St")
                    .phoneNumber("100-222-3333")
                    .build();
            givenPatient = patientRepository.saveAndFlush(givenPatient);
            givenPatientString = objectMapper.writeValueAsString(givenPatient);
            id = givenPatient.getId();
        }

        @AfterEach
        public void undefPerTest() {
            patientRepository.deleteAll();
            patientRepository.flush();
            id = 0;
            givenPatient = null;
            givenPatientString = null;
        }

        @SneakyThrows
        @Test
        @Tag("PatientControllerIT")
        @DisplayName("getPatient IT By Id should return patient")
        public void getPatientITByIdShouldReturnPatient() {

            //GIVEN
            //WHEN
            final MvcResult result = mvc.perform(get("/patients/" + id))
                    .andReturn();

            //THEN
            assertThat(result.getResponse().getStatus()).isEqualTo(200);
            assertThat(result.getResponse().getContentAsString()).contains(givenPatientString);
        }

        @SneakyThrows
        @ParameterizedTest(name = "{0} should throw a {1} exception")
        @CsvSource(
                value = {
                        "null, 'MethodArgumentTypeMismatchException'"
                        ,"-1, 'ConstraintViolationException'"
                        ,"0, 'ConstraintViolationException'"
                        ,"2147483648, 'ConstraintViolationException'"}
                ,nullValues = {"null"})
        @Tag("PatientControllerIT")
        @DisplayName("test getPatientById should throw Expected Exception")
        public void getPatientByIdTestShouldThrowExpectedException(Integer id, String exceptionName) {

            //GIVEN
            //WHEN
            final MvcResult result = mvc.perform(get("/patients/" + id))
                    .andReturn();

            //THEN
            assertThat(result.getResponse().getStatus()).isEqualTo(400);
            assertThat(result.getResponse().getContentAsString()).contains("\"message\":\"Bad request\"");
        }

        @SneakyThrows
        @Test
        @Tag("PatientControllerIT")
        @DisplayName("getPatient IT By Id should return a BadRequest Response if patient not found")
        public void getPatientITByIdShouldReturnABadRequestResponseIfNotFound() {

            //GIVEN
            //WHEN
            final MvcResult result = mvc.perform(get("/patients/" + id + 1))
                    .andReturn();

            //THEN
            assertThat(result.getResponse().getStatus()).isEqualTo(400);
            assertThat(result.getResponse().getContentAsString()).contains("\"message\":\"Bad request\"");
        }
    }

    @Nested
    @Tag("createPatient")
    @DisplayName("IT for POST /patients/")
    @TestInstance(TestInstance.Lifecycle.PER_CLASS)
    class CreatePatientIT {

        Patient givenPatient;

        @SneakyThrows
        @BeforeEach
        public void setUpPerTest() {
            givenPatient = Patient.builder()
                    .firstName("Test")
                    .lastName("TestNone")
                    .birthDate(LocalDate.of(1966, 12, 31))
                    .genre("F")
                    .address("1 Brookside St")
                    .phoneNumber("100-222-3333")
                    .build();
        }

        @AfterEach
        public void undefPerTest() {
            patientRepository.deleteAll();
            patientRepository.flush();
            givenPatient = null;
        }

        @SneakyThrows
        @Test
        @Tag("PatientControllerIT")
        @DisplayName("createPatient IT should return saved patient with id")
        public void createPatientITShouldReturnSaveAndReturnPatientWithId() {

            //GIVEN
            String body = objectMapper.writeValueAsString(givenPatient);
            String givenPatientString = objectMapper.writeValueAsString(givenPatient);

            //WHEN
            final MvcResult result = mvc.perform(post("/patients")
                            .contentType("application/json")
                            .content(body))
                    .andReturn();

            //THEN
            assertThat(result.getResponse().getStatus()).isEqualTo(201);
            Patient patientResult = objectMapper.readValue(result.getResponse().getContentAsString(), Patient.class);
            assertThat(patientResult.getId()).isGreaterThanOrEqualTo(1);
            assertThat(patientResult)
                    .extracting(
                            Patient::getFirstName,
                            Patient::getLastName,
                            p -> p.getBirthDate().format(DateTimeFormatter.BASIC_ISO_DATE),
                            Patient::getGenre,
                            Patient::getAddress,
                            Patient::getPhoneNumber)
                    .containsExactly("Test", "TestNone", "19661231", "F", "1 Brookside St", "100-222-3333");
        }

        @SneakyThrows
        @Test
        @Tag("PatientControllerIT")
        @DisplayName("createPatient IT should return a BadRequest Response if id not null")
        public void createPatientITShouldABadRequestResponseIfIdNotNull() {

            //GIVEN
            givenPatient.setId(1);
            String body = objectMapper.writeValueAsString(givenPatient);

            //WHEN
            final MvcResult result = mvc.perform(post("/patients")
                            .contentType("application/json")
                            .content(body))
                    .andReturn();

            //THEN
            assertThat(result.getResponse().getStatus()).isEqualTo(400);
            assertThat(result.getResponse().getContentAsString()).contains("\"message\":\"Bad request\"");
        }
    }

    @Nested
    @Tag("updatePatient")
    @DisplayName("IT for PUT /patients/")
    @TestInstance(TestInstance.Lifecycle.PER_CLASS)
    class UpdatePatientIT {

        Patient givenPatient;
        int id;

        @SneakyThrows
        @BeforeEach
        public void setUpPerTest() {
            givenPatient = Patient.builder()
                    .firstName("Test")
                    .lastName("TestNone")
                    .birthDate(LocalDate.of(1966, 12, 31))
                    .genre("F")
                    .address("1 Brookside St")
                    .phoneNumber("100-222-3333")
                    .build();
            id = patientRepository.saveAndFlush(givenPatient).getId();
        }

        @AfterEach
        public void undefPerTest() {
            patientRepository.deleteAll();
            patientRepository.flush();
            id = 0;
            givenPatient = null;
        }

        @SneakyThrows
        @Test
        @Tag("PatientControllerIT")
        @DisplayName("updatePatient IT should return updated patient")
        public void updatePatientITShouldReturnSaveAndReturnPatientWithId() {

            //GIVEN
            Patient patientUpdated = Patient.builder()
                    .id(id)
                    .firstName("Test")
                    .lastName("TestBorderline")
                    .birthDate(LocalDate.of(1945, 06, 24))
                    .genre("M")
                    .address("2 High St")
                    .phoneNumber("200-333-4444")
                    .build();
            String body = objectMapper.writeValueAsString(patientUpdated);
            String patientUpdatedString = objectMapper.writeValueAsString(patientUpdated);

            //WHEN
            final MvcResult result = mvc.perform(put("/patients")
                            .contentType("application/json")
                            .content(body))
                    .andReturn();

            //THEN
            assertThat(result.getResponse().getStatus()).isEqualTo(200);
            assertThat(result.getResponse().getContentAsString()).contains(patientUpdatedString);
        }

        @SneakyThrows
        @ParameterizedTest(name = "{0} should throw a Bad Request Response")
        @NullSource
        @MethodSource("stringArrayProvider")
        @Tag("PatientControllerIT")
        @DisplayName("updatePatient IT should return a BadRequest Response if id null or not found")
        public void updatePatientITShouldReturnABadRequestResponseIfIdNullOrNotFound(Integer idI) {
            //GIVEN
            Patient patientUpdated = Patient.builder()
                    .id(idI)
                    .firstName("Test")
                    .lastName("TestBorderline")
                    .birthDate(LocalDate.of(1945, 06, 24))
                    .genre("M")
                    .address("2 High St")
                    .phoneNumber("200-333-4444")
                    .build();
            String body = objectMapper.writeValueAsString(patientUpdated);

            //WHEN
            final MvcResult result = mvc.perform(put("/patients")
                            .contentType("application/json")
                            .content(body))
                    .andReturn();

            //THEN
            assertThat(result.getResponse().getStatus()).isEqualTo(400);
            assertThat(result.getResponse().getContentAsString()).contains("\"message\":\"Bad request\"");
        }

        private Stream<Arguments> stringArrayProvider() {
            return Stream.of(
                    arguments(id + 1)
            );
        }
    }

    @Nested
    @Tag("deleteById")
    @DisplayName("IT for DELETE /patients/{id}")
    @TestInstance(TestInstance.Lifecycle.PER_CLASS)
    class DeleteByIdTests {

        @SneakyThrows
        @Test
        @Tag("PatientControllerIT")
        @DisplayName("deletePatient IT By id should delete patient")
        public void deletePatientITByIdShouldDeletePatient() {

            //GIVEN
            Patient givenPatient = Patient.builder()
                    .firstName("Test")
                    .lastName("TestNone")
                    .birthDate(LocalDate.of(1966, 12, 31))
                    .genre("F")
                    .address("1 Brookside St")
                    .phoneNumber("100-222-3333")
                    .build();
            int id = patientRepository.saveAndFlush(givenPatient).getId();

            //WHEN
            final MvcResult result = mvc.perform(delete("/patients/" + id))
                    .andReturn();
            patientRepository.flush();

            //THEN
            assertThat(result.getResponse().getStatus()).isEqualTo(200);
            assertThat(patientRepository.findById(id)).isEmpty();
        }

        @SneakyThrows
        @Test
        @Tag("PatientControllerIT")
        @DisplayName("deletePatient IT By null id should return a BadRequest Response")
        public void deletePatientITByNullIdShouldReturnABadRequestResponse() {

            //GIVEN
            Integer id = null;

            //WHEN
            final MvcResult result = mvc.perform(delete("/patients/" + id))
                    .andReturn();

            //THEN
            assertThat(result.getResponse().getStatus()).isEqualTo(400);
            assertThat(result.getResponse().getContentAsString()).contains("\"message\":\"Bad request\"");
        }
    }

    @SneakyThrows
    @ParameterizedTest(name = "{0} should throw a Bad Request ResponseEntity")
    @NullSource
    @ValueSource(strings = {"-1", "0"})
    @Tag("PatientControllerIT")
    @DisplayName("@Valid in @PathVariable test with a incorrect id value should return a Bad Request ResponseEntity")
    void whenIdIsInvalidThenReturnsBadRequestResponse(String ids) {

        //GIVEN
        Integer id = ids != null ? Integer.valueOf(ids) : null;
        //WHEN
        MvcResult result = mvc.perform(get("/patients/" + id))
                .andReturn();
        //THEN
        assertThat(result.getResponse().getStatus()).isEqualTo(400);
        //content is a ApiError Json
        assertThat(result.getResponse().getContentAsString()).contains("\"message\":\"Bad request\"");
    }

    @SneakyThrows
    @ParameterizedTest(name = "{0} should throw a Bad Request ResponseEntity")
    @NullAndEmptySource
    @ValueSource(strings = {"   ", "AbcdefghijklmnopqrstuvwxyzAbcdefghijklmnopqrstuvwxyz"})//26*2=52
    @Tag("PatientControllerIT")
    @DisplayName("@Valid in @RequestBody test with a incorrect patient firstName should return a Bad Request ResponseEntity")
    void whenPatientIsInvalidThenReturnsBadRequestResponse(String firstName) {

        //GIVEN
        Patient invalidPatient = Patient.builder()
                .firstName(firstName)
                .lastName("lastName")
                .birthDate(LocalDate.of(1950, 12, 31))
                .genre("M")
                .build();

        String body = objectMapper.writeValueAsString(invalidPatient);

        //WHEN
        MvcResult result = mvc.perform(post("/patients")
                        .contentType("application/json")
                        .content(body))
                .andReturn();
        //THEN
        assertThat(result.getResponse().getStatus()).isEqualTo(400);
        //content is a ApiError Json
        assertThat(result.getResponse().getContentAsString()).contains("\"message\":\"Bad request\"");
    }
}
