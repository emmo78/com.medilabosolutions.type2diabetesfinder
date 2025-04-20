package com.medilabosolutions.type2diabetesfinder.frontservice.controller;

import com.medilabosolutions.type2diabetesfinder.frontservice.model.Patient;
import com.medilabosolutions.type2diabetesfinder.frontservice.service.PatientFrontServiceImpl;
import com.medilabosolutions.type2diabetesfinder.frontservice.service.RequestService;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;

import static org.mockito.ArgumentMatchers.anyInt;

import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.mock.web.MockHttpServletRequest;

import org.springframework.ui.Model;
import org.springframework.web.context.request.ServletWebRequest;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.tuple;
import static org.mockito.Mockito.*;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

/**
 * Need the patientService running
 */
@ExtendWith(MockitoExtension.class)
class PatientFrontControllerTest {

    @Mock
    PatientFrontServiceImpl patientFrontService;

    @Mock
    Model model;

    @Spy
    RequestService requestService;

    @InjectMocks
    PatientFrontController patientFrontController;

    private MockHttpServletRequest requestMock;
    private ServletWebRequest request;

    @AfterEach
    public void tearDownPerTest() {
        patientFrontController = null;
    }

    @Nested
    @Tag("getPatients Tests")
    @DisplayName("Test for home")
    @TestInstance(TestInstance.Lifecycle.PER_CLASS)
    class HomeTest {

        private Pageable pageRequest;

        @BeforeAll
        public void setUpForAllTests() {
            pageRequest = Pageable.unpaged();
        }

        @AfterAll
        public void unSetForAllTests() {
            pageRequest = null;
        }

        @BeforeEach
        public void setUpForEachTests() {
            requestMock = new MockHttpServletRequest();
            requestMock.setServerName("http://localhost:9103");
            requestMock.setRequestURI("/front/home");
            request = new ServletWebRequest(requestMock);
        }

        @AfterEach
        public void unSetForEachTests() {
            requestMock = null;
            request = null;
        }

        @Test
        @Tag(PatientFrontControllerTest)
        @DisplayName("homePage should redirect to \"/front/home\"")
        public void homePageShouldRedirectToHome() {
            // GIVEN
            // WHEN
            String page = patientFrontController.homePage();
            // THEN
            assertThat(page).isEqualTo("redirect:/front/home");

        }

        @Test
        @Tag("PatientFrontControllerTest")
        @DisplayName("getPatients Test should return page of patients")
        public void homeTestPatientsShouldReturnPageOfPatients() {

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
            ArgumentCaptor<String> stringArgumentCaptor = ArgumentCaptor.forClass(String.class);
            ArgumentCaptor<Iterable<Object>> iterableArgumentCaptor = ArgumentCaptor.forClass(Iterable.class);
            when(patientFrontService.getPatients(anyInt())).thenReturn(new PageImpl<>(givenPatients, pageRequest, 4));

            //WHEN
            String page = patientFrontController.home(Optional.of("0"), model, request);

            //THEN
            assertThat(page).isEqualTo("home");
            verify(model, times(2)).addAttribute(stringArgumentCaptor.capture(), iterableArgumentCaptor.capture()); //times(1) is used by default
            assertThat(stringArgumentCaptor.getAllValues()).containsExactly("patients", "pageInterval");
            assertThat(iterableArgumentCaptor.getAllValues().get(0))
                    .extracting(
                            o -> ((Patient)o).getId(),
                            o -> ((Patient)o).getFirstName(),
                            o -> ((Patient)o).getLastName(),
                            o -> ((Patient)o).getBirthDate().format(DateTimeFormatter.BASIC_ISO_DATE),
                            o -> ((Patient)o).getGenre(),
                            o -> ((Patient)o).getAddress(),
                            o -> ((Patient)o).getPhoneNumber())
                    .contains(
                            tuple(1, "Test", "TestNone", "19661231", "F", "1 Brookside St", "100-222-3333")
                            , tuple(2, "Test", "TestBorderline", "19450624", "M", "2 High St", "200-333-4444")
                            , tuple(3, "Test", "TestDanger", "20040618", "M", "3 Club Road", "300-444-5555")
                            , tuple(4, "Test", "TestEarlyOnset", "20020628", "F", "4 Valley Dr", "400-555-6666")
                    );
        }
    }

    @Nested
    @Tag("createpatient Test")
    @DisplayName("Test for createpatient")
    class CreatePatientTest {

        @BeforeEach
        public void setUpForEachTests() {
            requestMock = new MockHttpServletRequest();
            requestMock.setServerName("http://localhost:9103");
            requestMock.setRequestURI("/front/createpatient");
            request = new ServletWebRequest(requestMock);
        }

        @AfterEach
        public void unSetForEachTests() {
            requestMock = null;
            request = null;
        }


        @Test
        @Tag("PatientFrontControllerTest")
        @DisplayName("createPatient Test should return a valid form view")
        public void createPatientTestShouldReturnFormView() {
            // GIVEN
            // WHEN
            String page = patientFrontController.createPatient(new Patient());
            // THEN
            assertThat(page).isEqualTo("formnewpatient");
        }
    }

    @Nested
    @Tag("updatePatient Test")
    @DisplayName("Test for updatepatient")
    class UpdatePatientTest {

        @BeforeEach
        public void setUpForEachTests() {
            requestMock = new MockHttpServletRequest();
            requestMock.setServerName("http://localhost:9103");
            requestMock.setRequestURI("updatepatientTest");
            request = new ServletWebRequest(requestMock);
        }

        @AfterEach
        public void unSetForEachTests() {
            requestMock = null;
            request = null;
        }

        @Test
        @Tag("PatientFrontControllerTest")
        @DisplayName("updatePatient Test should return a valid form view")
        void updatePatientTestShouldReturnFormView() {

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
            ArgumentCaptor<String> stringArgumentCaptor = ArgumentCaptor.forClass(String.class);
            ArgumentCaptor<Patient> patientArgumentCaptor = ArgumentCaptor.forClass(Patient.class);
            when(patientFrontService.getPatient(anyInt())).thenReturn(givenPatient);


            // WHEN
            String page = patientFrontController.updatePatient(1, model, request);

            // THEN
            assertThat(page).isEqualTo("formupdatepatient");
            verify(model).addAttribute(stringArgumentCaptor.capture(), patientArgumentCaptor.capture()); //times(1) is used by default
            assertThat(stringArgumentCaptor.getValue()).isEqualTo("patient");
            assertThat(patientArgumentCaptor.getValue())
                    .extracting(
                            Patient::getId,
                            Patient::getFirstName,
                            Patient::getLastName,
                    p -> p.getBirthDate().format(DateTimeFormatter.BASIC_ISO_DATE),
                            Patient::getGenre,
                            Patient::getAddress,
                            Patient::getPhoneNumber)
                    .contains(
                            1, "Test", "TestNone", "19661231", "F", "1 Brookside St", "100-222-3333"
                    );
        }
    }

    @Nested
    @Tag("deletepatient Test")
    @DisplayName("Test for deletepatient")
    class DeletePatientTest {

        @BeforeEach
        public void setUpForEachTests() {
            requestMock = new MockHttpServletRequest();
            requestMock.setServerName("http://localhost:8080");
            requestMock.setRequestURI("/front/deletepatient/{id}");
            request = new ServletWebRequest(requestMock);
        }

        @AfterEach
        public void unSetForEachTests() {
            requestMock = null;
            request = null;
        }

        @Test
        @Tag("PatientFrontController Test")
        @DisplayName("deletePatient Test should remove patient from system silently")
        void deletePatientTestShouldRemovePatientFromSystemSilently() {

            // GIVEN
            Patient patientToDelete = Patient.builder()
                    .firstName("ToDelete")
                    .lastName("Patient")
                    .birthDate(LocalDate.of(1980, 1, 1))
                    .genre("M")
                    .address("123 Delete St")
                    .phoneNumber("555-666-7777")
                    .build();
            int id = patientProxy.createPatient(patientToDelete).getId();

            // WHEN
            final MvcResult result = mvc.perform(get("/front/deletepatient/" + id)).andReturn();

            // THEN
            try {
                // The 302 Found redirect response status code indicates that the resource is temporarily moved to the home URL
                assertThat(result.getResponse().getStatus()).isEqualTo(302);
                assertThat(result.getResponse().getHeader("Location")).isEqualTo("/");

                // Verify the patient no longer exists
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

            } finally {
                patientProxy.deletePatient(id);
            }
        }
    }
*/}

