package com.medilabosolutions.type2diabetesfinder.frontservice.service;

import com.medilabosolutions.type2diabetesfinder.frontservice.model.Patient;
import com.medilabosolutions.type2diabetesfinder.frontservice.repository.PatientProxy;
import jakarta.inject.Inject;
import org.junit.jupiter.api.*;
import org.springframework.boot.test.context.SpringBootTest;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.tuple;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.when;

@SpringBootTest
public class PatientFrontServiceTest {

    @Inject
    PatientFrontServiceImpl patientFrontService;

    //@MockBean is deprecated since SpringBoot 3.4.0
    @MockitoBean
    PatientProxy patientProxy;

    @AfterEach
    public void unSetForEachTests() {
        patientFrontService = null;
    }

    @Nested
    @Tag("getPatientsTests")
    @DisplayName("Test for getPatients")
    @TestInstance(TestInstance.Lifecycle.PER_CLASS)
    class GetPatientsTest {

        private Pageable pageRequest;

        @BeforeAll
        public void setUpForAllTests() {
            pageRequest = Pageable.unpaged();
        }

        @AfterAll
        public void unSetForAllTests() {
            pageRequest = null;
        }

        @Test
        @Tag("PatientFrontServiceTest")
        @DisplayName("getPatients Test should return patients")
        public void getPatientsTestShouldReturnPatients() {
            //GIVEN
            List<Patient> givenPatients = List.of(
                    Patient.builder()
                            .id(1)
                            .firstName("Test")
                            .lastName("TestNone")
                            .birthDate(LocalDate.of(1966, 12, 31))
                            .genre("F")
                            .address("1 Brookside St")
                            .phoneNumber("100-222-3333")
                            .build(),
                    Patient.builder()
                            .id(2)
                            .firstName("Test")
                            .lastName("TestBorderline")
                            .birthDate(LocalDate.of(1945, 06, 24))
                            .genre("M")
                            .address("2 High St")
                            .phoneNumber("200-333-4444")
                            .build(),
                    Patient.builder()
                            .id(3)
                            .firstName("Test")
                            .lastName("TestDanger")
                            .birthDate(LocalDate.of(2004, 06, 18))
                            .genre("M")
                            .address("3 Club Road")
                            .phoneNumber("300-444-5555")
                            .build(),
                    Patient.builder()
                            .id(4)
                            .firstName("Test")
                            .lastName("TestEarlyOnset")
                            .birthDate(LocalDate.of(2002, 06, 28))
                            .genre("F")
                            .address("4 Valley Dr")
                            .phoneNumber("400-555-6666")
                            .build()
            );
            when(patientProxy.getPatients(any(Optional.class)))
                    .thenReturn(new ResponseEntity<Page<Patient>>(new PageImpl<>(givenPatients, pageRequest, 4), HttpStatus.OK));

            //WHEN
            Page<Patient> pagedPatient = patientFrontService.getPatients(0);

            //THEN
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
                            tuple(1, "Test", "TestNone", "19661231", "F", "1 Brookside St", "100-222-3333")
                            ,tuple(2, "Test", "TestBorderline", "19450624", "M", "2 High St", "200-333-4444")
                            ,tuple(3, "Test", "TestDanger", "20040618", "M", "3 Club Road", "300-444-5555")
                            ,tuple(4, "Test", "TestEarlyOnset", "20020628", "F", "4 Valley Dr", "400-555-6666")
                    );
        }
    }

    @Nested
    @Tag("getPatientTests")
    @DisplayName("Test for getPatient")
    class getPatientTest {

        Patient givenPatient;

        @BeforeEach
        public void setUpPerTest() {
            givenPatient = Patient.builder()
                    .id(1)
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
        @Tag("PatientFrontServiceTest")
        @DisplayName("getPatient Test should return patient")
        public void getPatientTestShouldReturnPatient() {

            //GIVEN
            when(patientProxy.getPatient(anyInt())).thenReturn(new ResponseEntity<>(givenPatient, HttpStatus.OK));
            when(patientProxy.getNotesByPatientId(anyInt())).thenReturn(new ResponseEntity<>(new ArrayList<>(), HttpStatus.OK));
            //WHEN
            Patient patientResult = patientFrontService.getPatient(1);
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
                            1, "Test", "TestNone", "19661231", "F", "1 Brookside St", "100-222-3333"
                    );
        }
    }

    @Nested
    @Tag("createPatientTests")
    @DisplayName("Test for createPatient")
    class CreatePatientTest {

        Patient givenPatient;

        @BeforeEach
        public void setUpPerTest() {
            givenPatient = Patient.builder()
                    .id(1)
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
        @Tag("PatientFrontServiceTest")
        @DisplayName("createPatient Test should return saved patient with id")
        public void createPatientTestShouldReturnSaveAndReturnPatientWithId() {

            //GIVEN
            when(patientProxy.createPatient(any(Optional.class))).thenReturn(new ResponseEntity<>(givenPatient, HttpStatus.CREATED));
            //WHEN
            Patient patientResult = patientFrontService.createPatient(givenPatient);

            //THEN
            assertThat(patientResult)
                    .extracting(
                            Patient::getId,
                            Patient::getFirstName,
                            Patient::getLastName,
                            p -> p.getBirthDate().format(DateTimeFormatter.BASIC_ISO_DATE),
                            Patient::getGenre,
                            Patient::getAddress,
                            Patient::getPhoneNumber)
                    .containsExactly(1,"Test", "TestNone", "19661231", "F", "1 Brookside St", "100-222-3333");
        }
    }

    @Nested
    @Tag("updatePatientTest")
    @DisplayName("Test for updatePatient")
    class UpdatePatientTest {

        Patient givenPatient;

        @BeforeEach
        public void setUpPerTest() {
            givenPatient = Patient.builder()
                    .id(1)
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
        @Tag("PatientFrontServiceTest")
        @DisplayName("updatePatient Test should return updated patient")
        public void updatePatientTestShouldReturnSaveAndReturnPatientWithId() {

            //GIVEN
            when(patientProxy.updatePatient(any(Optional.class))).thenReturn(new ResponseEntity<>(givenPatient, HttpStatus.OK));
            //WHEN
            Patient patientResult = patientFrontService.updatePatient(givenPatient);
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
                            1, "Test", "TestNone", "19661231", "F", "1 Brookside St", "100-222-3333"
                    );
        }
    }

    @Nested
    @Tag("deletePatientTests")
    @DisplayName("Test for deletePatient")
    class DeletePatientTest {

        @Test
        @Tag("PatientFrontServiceTest")
        @DisplayName("deletePatient Test should delete the patient")
        public void deletePatientTestShouldDeleteIt() {
            // GIVEN
            Patient givenPatient = Patient.builder()
                    .id(1)
                    .firstName("Test")
                    .lastName("TestNone")
                    .birthDate(LocalDate.of(1966, 12, 31))
                    .genre("F")
                    .address("1 Brookside St")
                    .phoneNumber("100-222-3333")
                    .build();
            // WHEN
            assertDoesNotThrow(() -> patientFrontService.deletePatient(1));
        }
    }

}
