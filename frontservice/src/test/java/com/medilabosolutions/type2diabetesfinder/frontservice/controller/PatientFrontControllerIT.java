package com.medilabosolutions.type2diabetesfinder.frontservice.controller;

import com.medilabosolutions.type2diabetesfinder.frontservice.configuration.UrlApiProperties;
import com.medilabosolutions.type2diabetesfinder.frontservice.model.Patient;
import com.medilabosolutions.type2diabetesfinder.frontservice.repository.PatientProxy;
import com.medilabosolutions.type2diabetesfinder.frontservice.service.PatientFrontService;
import com.medilabosolutions.type2diabetesfinder.frontservice.service.RequestService;
import jakarta.inject.Inject;
import lombok.SneakyThrows;
import org.assertj.core.groups.Tuple;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.InjectMocks;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.data.domain.Page;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.web.context.request.ServletWebRequest;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.tuple;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

/**
 * Need the patientService running
 */
@ExtendWith(SpringExtension.class)
@SpringBootTest
@AutoConfigureMockMvc
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
        @ParameterizedTest(name = "{0} should first page of three and then page of one")
        @ValueSource( strings = {"0", "1"})
        @Tag("PatientFrontControllerIT")
        @DisplayName("getPatients Test should return selected page of patients")
        public void homeTestPatients(String pageNumber) {

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
            List<Integer> ids = new ArrayList<>();
            givenPatients.forEach(patient -> {
                int id = patientProxy.createPatient(patient).getId();
                patient.setId(id);
                ids.add(id);
            });

            List<Tuple> expectedResult = "0".equals(pageNumber)?
                    List.of(
                            tuple(ids.get(0), "Test", "TestNone", "19661231", "F", "1 Brookside St", "100-222-3333")
                           ,tuple(ids.get(1), "Test", "TestBorderline", "19450624", "M", "2 High St", "200-333-4444")
                           ,tuple(ids.get(2), "Test", "TestDanger", "20040618", "M", "3 Club Road", "300-444-5555")

                    ):
                    List.of(
                            tuple(ids.get(3), "Test", "TestEarlyOnset", "20020628", "F", "4 Valley Dr", "400-555-6666")
                    );

            //WHEN
            final MvcResult result = mvc.perform(get("/?pageNumber="+pageNumber))
                    .andReturn();

            //THEN
            try {
                assertThat(result.getResponse().getStatus()).isEqualTo(200);
                assertThat((result.getResponse().getContentAsString())).contains("<title>home</title>");
                Page<Patient> resultPatients = (Page) result.getModelAndView().getModel().get("patients");
                assertThat(resultPatients).extracting(
                    Patient::getId,
                    Patient::getFirstName,
                    Patient::getLastName,
                    p -> p.getBirthDate().format(DateTimeFormatter.BASIC_ISO_DATE),
                    Patient::getGenre,
                    Patient::getAddress,
                    Patient::getPhoneNumber)
                .containsExactlyElementsOf(expectedResult);
            } finally {
                givenPatients.forEach(patient -> patientProxy.deletePatient(patient.getId()));
            }
        }
        //ToDo : @Test with "a" in ExceptionControllerIT
    }

    @Nested
    @Tag("createpatient form")
    @DisplayName("Test for \"/createpatient\"")
    class CreatePatientTest {

        @BeforeEach
        public void setUpForEachTests() {
            requestMock = new MockHttpServletRequest();
            requestMock.setServerName("http://localhost:8080");
            requestMock.setRequestURI("/createpatient");
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
        @DisplayName("createPatient Test should return a valid form view")
        public void createPatientTestShouldReturnFormView() {
            // GIVEN
            // WHEN
            final MvcResult result = mvc.perform(get("/createpatient")).andReturn();

            // THEN
            assertThat(result.getResponse().getStatus()).isEqualTo(200);
            assertThat(result.getResponse().getContentAsString()).contains("<title>formNewPatient</title>");
            assertThat((Patient) result.getModelAndView().getModel().get("patient"))
                    .isNotNull()
                    .extracting(
                            Patient::getId,
                            Patient::getFirstName,
                            Patient::getLastName,
                            Patient::getBirthDate,
                            Patient::getGenre,
                            Patient::getAddress,
                            Patient::getPhoneNumber
                    )
                    .containsExactly(
                            null, null, null, null, null, null, null
                    );
        }
    }

    @Nested
    @Tag("updatepatient form")
    @DisplayName("Test for \"/updatepatient/{id}\"")
    class UpdatePatientTest {

        @BeforeEach
        public void setUpForEachTests() {
            requestMock = new MockHttpServletRequest();
            requestMock.setServerName("http://localhost:8080");
            requestMock.setRequestURI("/updatepatient/{id}");
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
        @DisplayName("updatepatient Test should return a valid form view completed with patient to update")
        void updatePatientTestShouldReturnFormView() {

            // GIVEN
            Patient patientToUpdate = Patient.builder()
                    .firstName("Test")
                    .lastName("TestNone")
                    .birthDate(LocalDate.of(1966, 12, 31))
                    .genre("F")
                    .address("1 Brookside St")
                    .phoneNumber("100-222-3333")
                    .build();
            int id = patientProxy.createPatient(patientToUpdate).getId();

            // WHEN
            final MvcResult result = mvc.perform(get("/updatepatient/"+id)).andReturn();

            // THEN
            try {
                assertThat(result.getResponse().getStatus()).isEqualTo(200);
                assertThat(result.getResponse().getContentAsString()).contains("<title>formUpdatePatient</title>");
                Patient patientResult = (Patient) result.getModelAndView().getModel().get("patient");
                assertThat(patientResult)
                        .extracting(
                                Patient::getId
                               ,Patient::getFirstName
                               ,Patient::getLastName
                               ,p -> p.getBirthDate().format(DateTimeFormatter.BASIC_ISO_DATE)
                               ,Patient::getGenre
                               ,Patient::getAddress
                               ,Patient::getPhoneNumber
                        ).containsExactly(
                                id
                               ,"Test"
                               ,"TestNone"
                               ,"19661231"
                               ,"F"
                               ,"1 Brookside St"
                               ,"100-222-3333"
                       );
            } finally {
                patientProxy.deletePatient(id);
            }

        }
    }
}

