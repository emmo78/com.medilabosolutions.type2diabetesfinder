package com.medilabosolutions.type2diabetesfinder.frontservice.repository;

import com.medilabosolutions.type2diabetesfinder.frontservice.configuration.UrlApiProperties;
import com.medilabosolutions.type2diabetesfinder.frontservice.model.Patient;
import lombok.SneakyThrows;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.web.PagedModel;
import org.springframework.test.web.servlet.MvcResult;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

/**
 * Need the patientService running
 */
@ExtendWith(MockitoExtension.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class PatientProxyIT {

    @InjectMocks
    PatientProxy patientProxy;
    
    @Mock
    UrlApiProperties urlApiProperties;
    
    @BeforeAll
    public void setUp() {
        when(urlApiProperties.getApiURL()).thenReturn("http://localhost:9090");
    }
    
    @AfterEach
    public void tearDown() {
        urlApiProperties = null;
    }

    @Nested
    @Tag("createPatient")
    @DisplayName("IT for createPatient")
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
        @Tag("PatientProxyIT")
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
            assertThat(result.getResponse().getStatus()).isEqualTo(200);
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
        @Tag("PatientProxyIT")
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
            assertThat(
                    assertDoesNotThrow(() ->
                            result.getResponse().getContentAsString())
            ).contains("\"message\":\"Bad request\"");
        }
    }



    @Test
    @Tag("PatientProxyIT")
    @DisplayName("getPatients IT should return patients")
    public void getPatientsITShouldReturnPatients() {

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
        

        //WHEN
        PagedModel<Patient> pageModelPatient = patientProxy.getPatients();

        //THEN
        //assertThat(result.getResponse().getStatus()).isEqualTo(200);
        //assertThat(
        //        assertDoesNotThrow(() ->
        //                result.getResponse().getContentAsString())
        //).contains(givenPatientsString);
    }



}
