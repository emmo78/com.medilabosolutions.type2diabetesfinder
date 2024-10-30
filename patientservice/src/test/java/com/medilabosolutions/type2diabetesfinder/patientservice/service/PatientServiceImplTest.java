package com.medilabosolutions.type2diabetesfinder.patientservice.service;

import com.medilabosolutions.type2diabetesfinder.patientservice.model.Patient;
import com.medilabosolutions.type2diabetesfinder.patientservice.repository.PatientRepository;
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

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.tuple;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * unit test class for the PatientService.
 * @author olivier morel
 */
@ExtendWith(MockitoExtension.class)
public class PatientServiceImplTest {

    @InjectMocks
    private PatientServiceImpl patientService;

    @Mock
    private PatientRepository patientRepository;

    private Patient patient;

    @Nested
    @Tag("getPatientsTests")
    @DisplayName("Tests for getting patients")
    @TestInstance(TestInstance.Lifecycle.PER_CLASS)
    class GetPatientsTests {

        private Pageable pageRequest;

        @BeforeAll
        public void setUpForAllTests() {
            pageRequest = Pageable.unpaged();
        }

        @AfterAll
        public void unSetForAllTests() {
            pageRequest = null;
        }

        @AfterEach
        public void unSetForEachTests() {
            patientService = null;
            patient = null;
        }

        @Test
        @Tag("PatientServiceTest")
        @DisplayName("test getPatients should return patients")
        public void getPatientsTestShouldReturnExpectedPatients() {

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
            when(patientRepository.findAll(any(Pageable.class))).thenReturn(new PageImpl<Patient>(givenPatients, pageRequest, 4));

            //WHEN
            Page<Patient> resultedPatients = patientService.getPatients(pageRequest);

            //THEN
            assertThat(resultedPatients)
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
        @Tag("PatientServiceTest")
        @DisplayName("test getPatients should throw NullPointerException")
        public void getPatientsTestShouldThrowsNullPointerException() {
            //GIVEN
            when(patientRepository.findAll(any(Pageable.class))).thenThrow(new NullPointerException());
            //WHEN
            //THEN
            assertThrows(NullPointerException.class,
                    () -> patientService.getPatients(pageRequest))
                    .getMessage();
        }
    }

    @Nested
    @Tag("getPatientTests")
    @DisplayName("Tests for getting patient")
    class GetPatientTests {

        @AfterEach
        public void unSetForEachTests() {
            patientService = null;
            patient = null;
        }

        @Test
        @Tag("PatientServiceTest")
        @DisplayName("test getPatient should return expected patient")
        public void getPatientTestShouldReturnExpectedPatient() {

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
            when(patientRepository.findById(anyInt())).thenReturn(Optional.of(patient));

            //WHEN
            Patient patientResult = patientService.getPatient(1);

            //THEN
            assertThat(patientResult).extracting(
                            Patient::getId,
                            Patient::getFirstName,
                            Patient::getLastName,
                            p -> p.getBirthDate().format(DateTimeFormatter.BASIC_ISO_DATE),
                            Patient::getGenre,
                            Patient::getAddress,
                            Patient::getPhoneNumber)
                    .containsExactly(
                            1,
                            "Test",
                            "TestNone",
                            "19661231",
                            "F",
                            "1 Brookside St",
                            "100-222-3333");
        }

        @Test
        @Tag("PatientServiceTest")
        @DisplayName("test getPatient should throw ResourceNotFoundException")
        public void getPatientTestShouldThrowsResourceNotFoundException() {
            //GIVEN
            when(patientRepository.findById(anyInt())).thenReturn(Optional.empty());

            //WHEN
            //THEN
            assertThat(assertThrows(ResourceNotFoundException.class,
                    () -> patientService.getPatient(1))
                    .getMessage()).isEqualTo("Patient not found");
        }
    }

    @Nested
    @Tag("createPatientTests")
    @DisplayName("Tests for create patient")
    class CreatePatientTests {

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

        @AfterEach
        public void unSetForEachTests() {
            patientService = null;
            patient = null;
        }

        @Test
        @Tag("PatientServiceTest")
        @DisplayName("test createPatient should persist and return patient")
        public void createPatientTestShouldPersistAndReturnPatient() {

            //GIVEN
            Patient patientExpected = Patient.builder()
                    .id(1)
                    .firstName("Test")
                    .lastName("TestNone")
                    .birthDate(LocalDate.of(1966,12,31))
                    .genre("F")
                    .address("1 Brookside St")
                    .phoneNumber("100-222-3333")
                    .build();
            when(patientRepository.save(any(Patient.class))).thenReturn(patientExpected);

            //WHEN
            Patient resultedPatient = assertDoesNotThrow(()->patientService.createPatient(patient));

            //THEN
            assertThat(resultedPatient).extracting(
                            Patient::getId,
                            Patient::getFirstName,
                            Patient::getLastName,
                            p -> p.getBirthDate().format(DateTimeFormatter.BASIC_ISO_DATE),
                            Patient::getGenre,
                            Patient::getAddress,
                            Patient::getPhoneNumber)
                    .containsExactly(
                            1,
                            "Test",
                            "TestNone",
                            "19661231",
                            "F",
                            "1 Brookside St",
                            "100-222-3333");
        }

        @Test
        @Tag("PatientServiceTest")
        @DisplayName("test createPatient should throw BadRequestException on Not Null Id")
        public void createPatientTestShouldThrowsBadRequestExceptionOnNotNullId() {

            //GIVEN
            patient.setId(1);

            //WHEN
            //THEN
            assertThat(assertThrows(BadRequestException.class,
                    () -> patientService.createPatient(patient))
                    .getMessage()).isEqualTo("Patient Id is not null");
        }
    }

    @Nested
    @Tag("updatePatientTests")
    @DisplayName("Tests for update patient")
    class UpdatePatientTests {

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

        @AfterEach
        public void unSetForEachTests() {
            patientService = null;
            patient = null;
        }

        @Test
        @Tag("PatientServiceTest")
        @DisplayName("test updatePatient should persist and return patient")
        public void updatePatientTestShouldPersistAndReturnPatient() {

            //GIVEN
            Patient patientExpected = Patient.builder()
                    .id(1)
                    .firstName("TestUpdt")
                    .lastName("TestNoneUpdt")
                    .birthDate(LocalDate.of(1976,12,31))
                    .genre("M")
                    .address("10 Brookside St")
                    .phoneNumber("200-222-3333")
                    .build();
            when(patientRepository.existsById(anyInt())).thenReturn(true);
            when(patientRepository.save(any(Patient.class))).thenReturn(patientExpected);

            //WHEN
            Patient resultedPatient = patientService.updatePatient(patient);

            //THEN
            assertThat(resultedPatient).extracting(
                            Patient::getId,
                            Patient::getFirstName,
                            Patient::getLastName,
                            p -> p.getBirthDate().format(DateTimeFormatter.BASIC_ISO_DATE),
                            Patient::getGenre,
                            Patient::getAddress,
                            Patient::getPhoneNumber)
                    .containsExactly(
                            1,
                            "TestUpdt",
                            "TestNoneUpdt",
                            "19761231",
                            "M",
                            "10 Brookside St",
                            "200-222-3333");
        }

        @Test
        @Tag("PatientServiceTest")
        @DisplayName("test updatePatient should throw ResourceNotFoundException")
        public void updatePatientTestShouldThrowsResourceNotFoundException() {

            //GIVEN
            when(patientRepository.existsById(anyInt())).thenReturn(false);

            //WHEN
            //THEN
            assertThat(assertThrows(ResourceNotFoundException.class,
                    () -> patientService.updatePatient(patient))
                    .getMessage()).isEqualTo("Patient not found for update");
        }
    }
}



