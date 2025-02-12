package com.medilabosolutions.type2diabetesfinder.patientservice.controller;

import com.medilabosolutions.type2diabetesfinder.patientservice.model.Patient;
import com.medilabosolutions.type2diabetesfinder.patientservice.service.PatientService;
import com.medilabosolutions.type2diabetesfinder.patientservice.service.RequestService;
import com.medilabosolutions.type2diabetesfinder.patientservice.service.RequestServiceImpl;
import org.apache.coyote.BadRequestException;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.InvalidDataAccessApiUsageException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.rest.webmvc.ResourceNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.context.request.WebRequest;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.tuple;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;

/**
 * unit test class for the PatientController.
 * @author olivier morel
 */
@ExtendWith(MockitoExtension.class)
public class PatientControllerTest {

    @InjectMocks
    private PatientController patientController;

    @Mock
    private PatientService patientService;

    @Spy
    private final RequestService requestService = new RequestServiceImpl();

    private MockHttpServletRequest requestMock;
    private WebRequest request;
    private Patient patient;

    @AfterEach
    public void unsetForEachTest() {
        patientService = null;
        patientController = null;
        patient = null;
    }

    @Nested
    @Tag("getPatients")
    @DisplayName("Tests for GET /patients")
    @TestInstance(TestInstance.Lifecycle.PER_CLASS)
    class GetPatientsTests {

        private Pageable pageRequest;

        @BeforeAll
        public void setUpForAllTests() {
            pageRequest = Pageable.unpaged();
            requestMock = new MockHttpServletRequest();
            requestMock.setMethod("GET");
            requestMock.setServerName("http://localhost:9090");
            requestMock.setRequestURI("/patients");
            request = new ServletWebRequest(requestMock);
        }

        @AfterAll
        public void unSetForAllTests() {
            pageRequest = null;
            requestMock = null;
            request = null;
        }

        @Test
        @Tag("PatientControllerTest")
        @DisplayName("test getPatients should return a Success ResponseEntity With Iterable Of Patient")
        public void getPatientsTestShouldReturnSuccessResponseEntityWithIterableOfPatient() {

            //GIVEN
            List<Patient> givenPatients = List.of(
                    Patient.builder()
                            .id(1)
                            .firstName("Test")
                            .lastName("TestNone")
                            .birthDate(LocalDate.of(1966,12,31))
                            .genre("F")
                            .address("1 Brookside St")
                            .phoneNumber("100-222-3333")
                            .build(),
                    Patient.builder()
                            .id(2)
                            .firstName("Test")
                            .lastName("TestBorderline")
                            .birthDate(LocalDate.of(1945,06,24))
                            .genre("M")
                            .address("2 High St")
                            .phoneNumber("200-333-4444")
                            .build(),
                    Patient.builder()
                            .id(3)
                            .firstName("Test")
                            .lastName("TestDanger")
                            .birthDate(LocalDate.of(2004,06,18))
                            .genre("M")
                            .address("3 Club Road")
                            .phoneNumber("300-444-5555")
                            .build(),
                    Patient.builder()
                            .id(4)
                            .firstName("Test")
                            .lastName("TestEarlyOnset")
                            .birthDate(LocalDate.of(2002,06,28))
                            .genre("F")
                            .address("4 Valley Dr")
                            .phoneNumber("400-555-6666")
                            .build()
            );

            when(patientService.getPatients(any(Pageable.class))).thenReturn(new PageImpl<Patient>(givenPatients, pageRequest, 4));

            //WHEN
            ResponseEntity<Page<Patient>> responseEntity = patientController.getPatients(Optional.of("3"), request);

            //THEN
            assertThat(responseEntity.getStatusCode().is2xxSuccessful()).isTrue();
            Iterable<Patient> resultPatients = responseEntity.getBody().getContent();
            assertThat(resultPatients).isNotNull();
            assertThat(resultPatients)
                    .extracting(
                            Patient::getId,
                            Patient::getFirstName,
                            Patient::getLastName,
                            p -> p.getBirthDate().format(DateTimeFormatter.BASIC_ISO_DATE),
                            Patient::getGenre,
                            Patient::getAddress,
                            Patient::getPhoneNumber)
                    .containsExactly(
                            tuple(1, "Test", "TestNone", "19661231", "F", "1 Brookside St", "100-222-3333"),
                            tuple(2, "Test", "TestBorderline", "19450624", "M", "2 High St", "200-333-4444"),
                            tuple(3, "Test", "TestDanger", "20040618", "M", "3 Club Road", "300-444-5555"),
                            tuple(4, "Test", "TestEarlyOnset", "20020628", "F", "4 Valley Dr", "400-555-6666"));
        }

        @Test
        @Tag("PatientControllerTest")
        @DisplayName("test getPatients should throw NullPointerException")
        public void getPatientsTestShouldThrowNullPointerException() {

            //GIVEN
            when(patientService.getPatients(any(Pageable.class))).thenThrow(new NullPointerException("Pageable must not be null"));

            //WHEN
            //THEN
            assertThat(assertThrows(NullPointerException.class,
                    () -> patientController.getPatients(Optional.ofNullable(null), request))
                   .getMessage()).isEqualTo("Pageable must not be null");
        }
    }

    @Nested
    @Tag("getPatientById")
    @DisplayName("Tests for GET /patients/{id}")
    @TestInstance(TestInstance.Lifecycle.PER_CLASS)
    class getPatientByIdTests {

        @BeforeAll
        public void setUpForAllTests() {
            requestMock = new MockHttpServletRequest();
            requestMock.setMethod("GET");
            requestMock.setServerName("http://localhost:9090");
            requestMock.setRequestURI("/patients/1");
            request = new ServletWebRequest(requestMock);
        }

        @AfterAll
        public void unSetForAllTests() {
            requestMock = null;
            request = null;
        }

        @Test
        @Tag("PatientControllerTest")
        @DisplayName("test getPatientById should return a Success ResponseEntity With Patient")
        public void getPatientByIdTestShouldReturnASuccessResponseEntityWithPatient() {

            //GIVEN
            patient = Patient.builder()
                    .id(1)
                    .firstName("Test")
                    .lastName("TestNone")
                    .birthDate(LocalDate.of(1966,12,31))
                    .genre("F")
                    .address("1 Brookside St")
                    .phoneNumber("100-222-3333")
                    .build();

            when(patientService.getPatient(anyInt())).thenReturn(patient);

            //WHEN
            ResponseEntity<Patient> responseEntity = patientController.getPatientById(1, request);

            //THEN
            assertThat(responseEntity.getStatusCode().is2xxSuccessful()).isTrue();
            Patient resultPatient = responseEntity.getBody();
            assertThat(resultPatient).isNotNull();
            assertThat(resultPatient)
                    .extracting(
                            Patient::getId,
                            Patient::getFirstName,
                            Patient::getLastName,
                            p -> p.getBirthDate().format(DateTimeFormatter.BASIC_ISO_DATE),
                            Patient::getGenre,
                            Patient::getAddress,
                            Patient::getPhoneNumber)
                    .containsExactly(
                            1, "Test", "TestNone", "19661231", "F", "1 Brookside St", "100-222-3333"
                    );
        }

        @Test
        @Tag("PatientControllerTest")
        @DisplayName("test getPatientById should throw (ResourceNotFoundException")
        public void getPatientByIdTestShouldThrowResourceNotFoundException() {

            //GIVEN
            when(patientService.getPatient(anyInt())).thenThrow(new ResourceNotFoundException("Patient not found"));
            //WHEN
            //THEN
            assertThat(assertThrows(ResourceNotFoundException.class,
                    () -> patientController.getPatientById(1, request))
                    .getMessage()).isEqualTo("Patient not found");
        }
    }

    @Nested
    @Tag("createPatient")
    @DisplayName("Tests for POST /patients/")
    @TestInstance(TestInstance.Lifecycle.PER_CLASS)
    class CreatePatientTests {

        @BeforeAll
        public void setUpForAllTests() {
            requestMock = new MockHttpServletRequest();
            requestMock.setMethod("POST");
            requestMock.setServerName("http://localhost:9090");
            requestMock.setRequestURI("/patients/");
            request = new ServletWebRequest(requestMock);
        }

        @AfterAll
        public void unSetForAllTests() {
            requestMock = null;
            request = null;
        }

        @BeforeEach
        public void setUpForEachTest() {
            patient = Patient.builder()
                    .id(null)
                    .firstName("Test")
                    .lastName("TestNone")
                    .birthDate(LocalDate.of(1966,12,31))
                    .genre("F")
                    .address("1 Brookside St")
                    .phoneNumber("100-222-3333")
                    .build();
        }

        @Test
        @Tag("PatientControllerTest")
        @DisplayName("test createPatient should return a Success ResponseEntity With Saved Patient")
        public void createPatientTestShouldReturnASuccessResponseEntityWithSavedPatient() {

            //GIVEN
            Optional<Patient> optionalPatient = Optional.of(patient);
            Patient patientExpected = Patient.builder()
                    .id(1)
                    .firstName("Test")
                    .lastName("TestNone")
                    .birthDate(LocalDate.of(1966,12,31))
                    .genre("F")
                    .address("1 Brookside St")
                    .phoneNumber("100-222-3333")
                    .build();
            try {
                when(patientService.createPatient(any(Patient.class))).thenReturn(patientExpected);
            } catch (BadRequestException e) {
                throw new RuntimeException(e);
            }

            //WHEN
            ResponseEntity<Patient> responseEntity =
                assertDoesNotThrow(()-> patientController.createPatient(optionalPatient, request));

            //THEN
            assertThat(responseEntity.getStatusCode().is2xxSuccessful()).isTrue();
            Patient resultPatient = responseEntity.getBody();
            assertThat(resultPatient).isNotNull();
            assertThat(resultPatient)
                    .extracting(
                            Patient::getId,
                            Patient::getFirstName,
                            Patient::getLastName,
                            p -> p.getBirthDate().format(DateTimeFormatter.BASIC_ISO_DATE),
                            Patient::getGenre,
                            Patient::getAddress,
                            Patient::getPhoneNumber)
                    .containsExactly(
                            1, "Test", "TestNone", "19661231", "F", "1 Brookside St", "100-222-3333"
                    );
        }

        @Test
        @Tag("PatientControllerTest")
        @DisplayName("test createPatient should throw BadRequestException")
        public void createPatientTestShouldThrowBadRequestException() {

            //GIVEN
            Optional<Patient> optionalPatient = Optional.empty();

            //WHEN
            //THEN
            assertThat(assertThrows(BadRequestException.class,
                    () -> patientController.createPatient(optionalPatient, request))
                    .getMessage()).isEqualTo("Correct request should be a json Patient body");
        }
    }

    @Nested
    @Tag("updatePatient")
    @DisplayName("Tests for PUT /patients/")
    @TestInstance(TestInstance.Lifecycle.PER_CLASS)
    class UpdatePatientTests {

        @BeforeAll
        public void setUpForAllTests() {
            requestMock = new MockHttpServletRequest();
            requestMock.setMethod("PUT");
            requestMock.setServerName("http://localhost:9090");
            requestMock.setRequestURI("/patients/1");
            request = new ServletWebRequest(requestMock);
        }

        @AfterAll
        public void unSetForAllTests() {
            requestMock = null;
            request = null;
        }

        @BeforeEach
        public void setUpForEachTest() {
            patient = Patient.builder()
                    .id(1)
                    .firstName("Test")
                    .lastName("TestNone")
                    .birthDate(LocalDate.of(1966,12,31))
                    .genre("F")
                    .address("1 Brookside St")
                    .phoneNumber("100-222-3333")
                    .build();
        }

        @Test
        @Tag("PatientControllerTest")
        @DisplayName("test updatePatient should return a Success ResponseEntity With Saved patient")
        public void updatePatientTestShouldReturnASuccessResponseEntityWithSavedPatient() {

            //GIVEN
            Optional<Patient> optionalPatient = Optional.of(patient);
            Patient patientExpected = Patient.builder()
                    .id(1)
                    .firstName("Test")
                    .lastName("TestBorderline")
                    .birthDate(LocalDate.of(1945,06,24))
                    .genre("M")
                    .address("2 High St")
                    .phoneNumber("200-333-4444")
                    .build();
            when(patientService.updatePatient(any(Patient.class))).thenReturn(patientExpected);

            //WHEN
            ResponseEntity<Patient> responseEntity =
                assertDoesNotThrow(()-> patientController.updatePatient(optionalPatient, request));

            //THEN
            assertThat(responseEntity.getStatusCode().is2xxSuccessful()).isTrue();
            Patient resultPatient = responseEntity.getBody();
            assertThat(resultPatient).isNotNull();
            assertThat(resultPatient)
                    .extracting(
                            Patient::getId,
                            Patient::getFirstName,
                            Patient::getLastName,
                            p -> p.getBirthDate().format(DateTimeFormatter.BASIC_ISO_DATE),
                            Patient::getGenre,
                            Patient::getAddress,
                            Patient::getPhoneNumber)
                    .containsExactly(
                            1, "Test", "TestBorderline", "19450624", "M", "2 High St", "200-333-4444"
                    );
        }

        @Test
        @Tag("PatientControllerTest")
        @DisplayName("test updatePatient should throw BadRequestException")
        public void updatePatientTestShouldThrowBadRequestException() {

            //GIVEN
            Optional<Patient> optionalPatient = Optional.empty();

            //WHEN
            //THEN
            assertThat(assertThrows(BadRequestException.class,
                    () -> patientController.updatePatient(optionalPatient, request))
                    .getMessage()).isEqualTo("Correct request should be a json Patient body");
        }

        @Test
        @Tag("PatientControllerTest")
        @DisplayName("test updatePatient should throw InvalidApiUsageException")
        public void updatePatientTestShouldThrowInvalidApiUsageException() {

            //GIVEN
            Optional<Patient> optionalPatient = Optional.of(patient);
            when(patientService.updatePatient(any(Patient.class))).thenThrow(new InvalidDataAccessApiUsageException("Error while saving patient"));

            //WHEN
            //THEN
            assertThat(assertThrows(InvalidDataAccessApiUsageException.class,
                    () -> patientController.updatePatient(optionalPatient, request))
                    .getMessage()).isEqualTo("Error while saving patient");
        }
    }

    @Nested
    @Tag("deleteByIdTests")
    @DisplayName("Tests for /api/patient/delete/{id}")
    @TestInstance(TestInstance.Lifecycle.PER_CLASS)
    class DeleteByIdTests {

        @BeforeAll
        public void setUpForAllTests() {
            requestMock = new MockHttpServletRequest();
            requestMock.setMethod("DELETE");
            requestMock.setServerName("http://localhost:8080");
            requestMock.setRequestURI("/api/patient/delete/1");
            request = new ServletWebRequest(requestMock);
        }

        @AfterAll
        public void unSetForAllTests() {
            requestMock = null;
            request = null;
        }

        @Test
        @Tag("PatientControllerTest")
        @DisplayName("test deleteById should return HttpStatus.OK")
        public void deleteByIdTestShouldReturnHttpStatusOK() {

            //GIVEN
            //WHEN
            HttpStatus httpStatus = patientController.deletePatientById(1, request);

            //THEN
            assertThat(httpStatus.is2xxSuccessful()).isTrue();
        }

        @Test
        @Tag("PatientControllerTest")
        @DisplayName("test deleteById should throw UnexpectedRollbackException")
        public void deleteByIdTestShouldThrowInvalidDataAccessApiUsageException() {

            //GIVEN
            doThrow(new InvalidDataAccessApiUsageException("Id must not be null")).when(patientService).deletePatient(nullable(Integer.class));
            //WHEN
            //THEN
            assertThat(assertThrows(InvalidDataAccessApiUsageException.class,
                    () -> patientController.deletePatientById(null, request))
                    .getMessage()).isEqualTo("Id must not be null");
        }
    }
}

