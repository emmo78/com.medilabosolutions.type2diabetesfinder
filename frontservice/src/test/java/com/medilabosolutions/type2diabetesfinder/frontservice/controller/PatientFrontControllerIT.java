package com.medilabosolutions.type2diabetesfinder.frontservice.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.medilabosolutions.type2diabetesfinder.frontservice.configuration.UrlApiProperties;
import com.medilabosolutions.type2diabetesfinder.frontservice.model.Patient;
import com.medilabosolutions.type2diabetesfinder.frontservice.repository.PatientProxy;
import com.medilabosolutions.type2diabetesfinder.frontservice.service.PatientFrontService;
import com.medilabosolutions.type2diabetesfinder.frontservice.service.RequestService;
import jakarta.inject.Inject;
import lombok.SneakyThrows;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.data.web.PagedModel;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.web.context.request.ServletWebRequest;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.tuple;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

/**
 * Need the patientService running
 */
@ExtendWith(SpringExtension.class)
@SpringBootTest
@AutoConfigureMockMvc
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class PatientFrontControllerIT {

    @Inject
    private MockMvc mvc;

    @SpyBean
    UrlApiProperties urlApiProperties;

    @SpyBean
    PatientProxy patientProxy;

    @SpyBean
    PatientFrontService patientFrontService;

    @SpyBean
    RequestService requestService;

    @InjectMocks
    PatientFrontController patientFrontController;

    private MockHttpServletRequest requestMock;
    private ServletWebRequest request;
    private ObjectMapper objectMapper;

    @BeforeAll
    public void init() {
        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
    }

    @AfterAll
    public void clean() {
        objectMapper = null;
    }

    @BeforeEach
    public void setUpPerTest() {
        when(urlApiProperties.getApiURL()).thenReturn("http://localhost:9090");
    }

    @AfterEach
    public void tearDownPerTest() {
        urlApiProperties = null;
    }

    @Nested
    @Tag("getPatients")
    @DisplayName("Test for \"/\"")
    class HomeTest {

        @BeforeEach
        public void setUpForEachTests() {
            requestMock = new MockHttpServletRequest();
            requestMock.setServerName("http://localhost:8080");
            requestMock.setRequestURI("/");
            request = new ServletWebRequest(requestMock);
        }

        @AfterEach
        public void unSetForEachTests() {
            requestMock = null;
            request = null;
        }

        @SneakyThrows
        @Test
        @Tag("PatientFrontControllerIT")
        @DisplayName("getPatients Test should return selected page of patients")
        public void homeTestPatients() {

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
            givenPatients.forEach(patient -> patient.setId(patientProxy.createPatient(patient).getId()));

            //WHEN
            final MvcResult result = mvc.perform(get("/"))
                    .andReturn();

            //THEN
            assertThat(result.getResponse().getStatus()).isEqualTo(200);
            List <Patient> resultPatients = ((PagedModel) result.getModelAndView().getModel().get("patients")).getContent();
            assertThat(resultPatients).extracting(
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
                    tuple(4, "Test", "TestEarlyOnset", "20020628", "F", "4 Valley Dr", "400-555-6666")
            );
            givenPatients.forEach(patient -> patientProxy.deletePatient(patient.getId()));
        }
    }

    /*@Nested
    @Tag("getPatient")
    @DisplayName("Test for \"/getPatient/{id}\"")
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

        @SneakyThrows
        @Test
        @Tag("PatientProxyTest")
        @DisplayName("getPatient Test should return patient")
        public void getPatientTestShouldReturnPatient() {

            //GIVEN
            //WHEN
            final MvcResult result = mvc.perform(get("/getPatient/" + id))
                    .andReturn();

            //THEN
            assertThat(result.getResponse().getStatus()).isEqualTo(200);

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
    @Tag("/patients")
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
        public void updatePatientTestShouldReturnABadRequestResponseIfIdNullOrNotFound(String idS) {
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
    }*/
}

