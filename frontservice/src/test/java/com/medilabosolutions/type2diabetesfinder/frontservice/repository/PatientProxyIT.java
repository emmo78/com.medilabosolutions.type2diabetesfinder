package com.medilabosolutions.type2diabetesfinder.frontservice.repository;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.medilabosolutions.type2diabetesfinder.frontservice.configuration.UrlApiProperties;
import com.medilabosolutions.type2diabetesfinder.frontservice.error.ApiError;
import com.medilabosolutions.type2diabetesfinder.frontservice.model.Patient;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.*;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.web.client.HttpClientErrorException;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.tuple;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

/**
 * Need the patientService running
 */
@ExtendWith(MockitoExtension.class)
class PatientProxyIT {

    @InjectMocks
    PatientProxy patientProxy;

    @Mock
    UrlApiProperties urlApiProperties;

    @BeforeEach
    public void setUp() {
        when(urlApiProperties.getApiURL()).thenReturn("http://localhost:9090");
    }

    @AfterEach
    public void tearDown() {
        urlApiProperties = null;
    }

    @Nested
    @Tag("createPatient")
    @DisplayName("Test for createPatient")
    class CreatePatientTest {

        Patient givenPatient;

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
            givenPatient = null;
        }

        @Test
        @Tag("PatientProxyTest")
        @DisplayName("createPatient Test should return saved patient with id")
        public void createPatientTestShouldReturnSaveAndReturnPatientWithId() {

            //GIVEN
            //WHEN
            Patient patientResult = patientProxy.createPatient(givenPatient);

            //THEN
            int id = patientResult.getId();
            try {
                assertThat(id).isGreaterThanOrEqualTo(1);
                assertThat(patientResult)
                        .extracting(
                                Patient::getFirstName,
                                Patient::getLastName,
                                p -> p.getBirthDate().format(DateTimeFormatter.BASIC_ISO_DATE),
                                Patient::getGenre,
                                Patient::getAddress,
                                Patient::getPhoneNumber)
                        .containsExactly("Test", "TestNone", "19661231", "F", "1 Brookside St", "100-222-3333");
            } finally {
                patientProxy.deletePatient(id);
            }
        }

        @Test
        @Tag("PatientProxyTest")
        @DisplayName("createPatient Test should throw HttpClientErrorException$BadRequest if id not null")
        public void createPatientTestShouldThrowAHttpClientErrorException$BadRequestIfIdNotNull() {

            //GIVEN
            givenPatient.setId(1);

            //WHEN
            //THEN
            HttpClientErrorException.BadRequest heeB = assertThrows(HttpClientErrorException.BadRequest.class, () -> patientProxy.createPatient(givenPatient));
            assertThat(heeB.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
            assertThat(heeB.getResponseBodyAs(ApiError.class))
                    .extracting(
                            ApiError::getMessage,
                            ApiError::getStatus
                    ).containsExactly(
                            "Bad request"
                            , HttpStatus.BAD_REQUEST
                    );
        }
    }

    @Nested
    @Tag("getPatient")
    @DisplayName("Test for getPatient")
    class getPatientTest {

        Patient givenPatient;
        int id;

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
            id = patientProxy.createPatient(givenPatient).getId();
        }

        @AfterEach
        public void undefPerTest() {
            patientProxy.deletePatient(id);
            givenPatient = null;
            id=0;
        }

        @Test
        @Tag("PatientProxyTest")
        @DisplayName("getPatient Test should return patient")
        public void getPatientTestShouldReturnPatient() {

            //GIVEN
            //WHEN
            Patient patientResult = patientProxy.getPatient(id);
            //THEN
            assertThat(patientResult).isNotNull();
            assertThat(patientResult)
                    .extracting(
                            Patient::getFirstName,
                            Patient::getLastName,
                            p -> p.getBirthDate().format(DateTimeFormatter.BASIC_ISO_DATE),
                            Patient::getGenre,
                            Patient::getAddress,
                            Patient::getPhoneNumber)
                    .containsExactly(
                            "Test", "TestNone", "19661231", "F", "1 Brookside St", "100-222-3333"
                    );
        }

        @Test
        @Tag("PatientProxyTest")
        @DisplayName("getPatient Test should throw a HttpClientErrorException.BadRequest if patient not found")
        public void getPatientTestShouldThrowHttpClientErrorException$BadRequestIfNotFound() {

            //GIVEN
            //WHEN
            //THEN
            HttpClientErrorException.BadRequest heeB = assertThrows(HttpClientErrorException.BadRequest.class, () -> patientProxy.getPatient(id+1));
            assertThat(heeB.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
            assertThat(heeB.getResponseBodyAs(ApiError.class))
                    .extracting(
                            ApiError::getMessage,
                            ApiError::getStatus
                    ).containsExactly(
                            "Bad request"
                            , HttpStatus.BAD_REQUEST
                    );
            }
        }

    @Nested
    @Tag("deletePatient")
    @DisplayName("Test for deletePatient")
    class DeletePatientTest {

        @Test
        @Tag("PatientProxyTest")
        @DisplayName("deletePatient Test should delete the patient")
        public void deletePatientTestShouldDeleteIt() {
            // GIVEN
            Patient givenPatient = Patient.builder()
                    .firstName("Test")
                    .lastName("TestNone")
                    .birthDate(LocalDate.of(1966, 12, 31))
                    .genre("F")
                    .address("1 Brookside St")
                    .phoneNumber("100-222-3333")
                    .build();
            int id = patientProxy.createPatient(givenPatient).getId();

            // WHEN
            assertDoesNotThrow(() -> patientProxy.deletePatient(id));

            // THEN
            HttpClientErrorException.BadRequest heeB =
                    assertThrows(HttpClientErrorException.BadRequest.class,
                                 () -> patientProxy.getPatient(id));
            assertThat(heeB.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
            assertThat(heeB.getResponseBodyAs(ApiError.class))
                    .extracting(
                            ApiError::getMessage,
                            ApiError::getStatus
                    ).containsExactly(
                            "Bad request"
                            , HttpStatus.BAD_REQUEST
                    );
        }
    }
    
    @Nested
    @Tag("getPatients")
    @DisplayName("Test for getPatients")
    class GetPatientsTest {

        @Test
        @Tag("PatientProxyTest")
        @DisplayName("getPatients Test should return patients")
        public void getPatientsITShouldReturnPatients() {

            //GIVEN
            ObjectMapper objectMapper = new ObjectMapper();
            List<Patient> givenPatients = List.of(
                    Patient.builder()
                            .firstName("Test")
                            .lastName("TestNone")
                            .birthDate(LocalDate.of(1966,12,31))
                            .genre("F")
                            .address("1 Brookside St")
                            .phoneNumber("100-222-3333")
                            .build(),
                    Patient.builder()
                            .firstName("Test")
                            .lastName("TestBorderline")
                            .birthDate(LocalDate.of(1945,06,24))
                            .genre("M")
                            .address("2 High St")
                            .phoneNumber("200-333-4444")
                            .build(),
                    Patient.builder()
                            .firstName("Test")
                            .lastName("TestDanger")
                            .birthDate(LocalDate.of(2004,06,18))
                            .genre("M")
                            .address("3 Club Road")
                            .phoneNumber("300-444-5555")
                            .build(),
                    Patient.builder()
                            .firstName("Test")
                            .lastName("TestEarlyOnset")
                            .birthDate(LocalDate.of(2002,06,28))
                            .genre("F")
                            .address("4 Valley Dr")
                            .phoneNumber("400-555-6666")
                            .build()
            );
            List<Integer> ids = new ArrayList<>();
            givenPatients.forEach(patient -> {
                int id = patientProxy.createPatient(patient).getId();
                patient.setId(id);
                ids.add(id);
            });

            //WHEN
            Page<Patient> pagedPatient = patientProxy.getPatients(0);

            //THEN
            try {
                assertThat(pagedPatient.getContent())
                        .extracting(
                                Patient::getId,
                                Patient::getFirstName,
                                Patient::getLastName,
                                p -> p.getBirthDate().format(DateTimeFormatter.BASIC_ISO_DATE),
                                Patient::getGenre,
                                Patient::getAddress,
                                Patient::getPhoneNumber)
                        .contains(
                                tuple(ids.get(0), "Test", "TestNone", "19661231", "F", "1 Brookside St", "100-222-3333")
                                ,tuple(ids.get(1), "Test", "TestBorderline", "19450624", "M", "2 High St", "200-333-4444")
                                ,tuple(ids.get(2), "Test", "TestDanger", "20040618", "M", "3 Club Road", "300-444-5555")
                                //,tuple(ids.get(3), "Test", "TestEarlyOnset", "20020628", "F", "4 Valley Dr", "400-555-6666")
                        );
            } finally {
                givenPatients.forEach(patient -> patientProxy.deletePatient(patient.getId()));
            }
       }
    }

    @Nested
    @Tag("updatePatient")
    @DisplayName("Test for updatePatient")
    class UpdatePatientTest {

        Patient givenPatient;
        int id;

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
            id = patientProxy.createPatient(givenPatient).getId();
        }

        @AfterEach
        public void undefPerTest() {
            patientProxy.deletePatient(id);
            givenPatient = null;
            id = 0;
        }

        @Test
        @Tag("PatientControllerTest")
        @DisplayName("updatePatient Test should return updated patient")
        public void updatePatientTestShouldReturnSaveAndReturnPatientWithId() {

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

            //WHEN
            Patient patientResult = patientProxy.updatePatient(patientUpdated);
            //THEN
            assertThat(patientResult).isNotNull();
            assertThat(patientResult)
                    .extracting(
                            Patient::getId,
                            Patient::getFirstName,
                            Patient::getLastName,
                            p -> p.getBirthDate().format(DateTimeFormatter.BASIC_ISO_DATE),
                            Patient::getGenre,
                            Patient::getAddress,
                            Patient::getPhoneNumber)
                    .containsExactly(
                            id, "Test", "TestBorderline", "19450624", "M", "2 High St", "200-333-4444"
                    );

        }

        @ParameterizedTest(name = "{0} should throw a Bad Request Response")
        @NullSource
        @ValueSource(strings = "id+1")
        @Tag("PatientControllerTest")
        @DisplayName("updatePatient Test should throw a HttpClientErrorException$BadRequest if id null or not found")
        public void updatePatientTestShouldThrowHttpClientErrorException$BadRequestIfIdNullNotFound(String idS) {
            //GIVEN
            Integer idI = idS != null ? id + 1 : null;
            Patient patientUpdated = Patient.builder()
                    .id(idI)
                    .firstName("Test")
                    .lastName("TestBorderline")
                    .birthDate(LocalDate.of(1945, 06, 24))
                    .genre("M")
                    .address("2 High St")
                    .phoneNumber("200-333-4444")
                    .build();

            //WHEN
            //THEN
            HttpClientErrorException.BadRequest heeB =
                    assertThrows(HttpClientErrorException.BadRequest.class,
                            () -> patientProxy.updatePatient(patientUpdated));
            assertThat(heeB.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
            assertThat(heeB.getResponseBodyAs(ApiError.class))
                    .extracting(
                            ApiError::getMessage,
                            ApiError::getStatus
                    ).containsExactly(
                            "Bad request"
                            , HttpStatus.BAD_REQUEST
                    );
        }
    }
}