package com.medilabosolutions.type2diabetesfinder.patientservice.repository;

import com.medilabosolutions.type2diabetesfinder.patientservice.model.Patient;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.InvalidDataAccessApiUsageException;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * integration test class to test unique constraint for userRepository,
 * also test JPA Named Query findByPatientname
 * and test thrown exception
 * nominal cases and for corner cases
 * @author olivier morel
 */
@SpringBootTest
@ActiveProfiles("mytest")
public class PatientRepositoryIT {
	
	@Autowired
	private PatientRepository patientRepository;

	private Patient patient;
	
	@BeforeEach
	public void setUpPerTest() {
		patient = new Patient();
	}
	
	@AfterEach
	public void undefPerTest() {
		patientRepository.deleteAll();
		patient = null;
	}
	
	@Nested
	@Tag("savePatientTests")
	@DisplayName("Tests for validation and saving patient")
	class savePatientTests {

		@Test
		@Tag("PatientRepositoryIT")
		@DisplayName("save test new user should persist user with new id")
		public void saveTestShouldPersistPatientWithNewId() {

			//GIVEN
			patient = Patient.builder()
					.id(null)
					.firstName("Test")
					.lastName("TestNone")
					.birthDate(LocalDate.of(1966,12,31))
					.genre("F")
					.address("1 Brookside St")
					.phoneNumber("100-222-3333")
					.build();

			//WHEN
			Patient patientResult = patientRepository.saveAndFlush(patient);

			//THEN
			Optional<Integer> idOpt = Optional.ofNullable(patientResult.getId());
			assertThat(idOpt).isPresent();
			idOpt.ifPresent(id -> assertThat(patientRepository.findById(id)).get().extracting(
							Patient::getFirstName,
							Patient::getLastName,
							p -> p.getBirthDate().format(DateTimeFormatter.BASIC_ISO_DATE),
							Patient::getGenre,
							Patient::getAddress,
							Patient::getPhoneNumber
							)
					.containsExactly(
							"Test",
							"TestNone",
							"19661231",
							"F",
							"1 Brookside St",
							"100-222-3333"));
		}

		@Test
		@Tag("PatientRepositoryIT")
		@DisplayName("save test update patient")
		public void saveTestUpdatePatientShouldPersistHim() {

			//GIVEN
			patient = Patient.builder()
					.id(null)
					.firstName("Test")
					.lastName("TestNone")
					.birthDate(LocalDate.of(1966,12,31))
					.genre("F")
					.address("1 Brookside St")
					.phoneNumber("100-222-3333")
					.build();

			Integer id = patientRepository.saveAndFlush(patient).getId();

			Patient updatedPatient = Patient.builder()
					.id(id)
					.firstName("TestUpdt")
					.lastName("TestNoneUpdt")
					.birthDate(LocalDate.of(1976,12,31))
					.genre("M")
					.address("10 Brookside St")
					.phoneNumber("200-222-3333")
					.build();

			//WHEN
			//THEN
			assertThat(assertDoesNotThrow(() -> patientRepository.saveAndFlush(updatedPatient)))
					.extracting(
							Patient::getId,
							Patient::getFirstName,
							Patient::getLastName,
							p -> p.getBirthDate().format(DateTimeFormatter.BASIC_ISO_DATE),
							Patient::getGenre,
							Patient::getAddress,
							Patient::getPhoneNumber)
					.containsExactly(
							id,
							"TestUpdt",
							"TestNoneUpdt",
							"19761231",
							"M",
							"10 Brookside St",
							"200-222-3333");
		}

		@Test
		@Tag("PatientRepositoryIT")
		@DisplayName("save test null should throw InvalidDataAccessApiUsageException")
		public void saveTestNull() {

			//GIVEN
			//WHEN
			//THEN
			assertThat(assertThrows(InvalidDataAccessApiUsageException.class, () -> patientRepository.save(null)).getMessage())
					.isEqualTo("Entity must not be null");

		}
	}

	@Test
	@Tag("PatientRepositoryIT")
	@DisplayName("find by Id null should throw an InvalidDataAccessApiUsageException")
	public void findByIdNullShouldThrowAnInvalidDataAccessApiUsageException() {
		//GIVEN
		//WHEN
		//THEN
		assertThat(assertThrows(InvalidDataAccessApiUsageException.class, () -> patientRepository.findById(null)).getMessage())
				.contains("The given id must not be null");
	}

	@Test
	@Tag("PatientRepositoryIT")
	@DisplayName("delete test null should throw InvalidDataAccessApiUsageException")
	public void deleteTestNull() {

		//GIVEN
		patient = null;
		//WHEN
		//THEN
		assertThat(assertThrows(InvalidDataAccessApiUsageException.class, () -> patientRepository.delete(patient)).getMessage())
				.isEqualTo("Entity must not be null");

	}

	@Test
	@Tag("PatientRepositoryIT")
	@DisplayName("existById test null should throw InvalidDataAccessApiUsageException")
	public void existByIdTestNull() {

		//GIVEN
		//WHEN
		//THEN
		assertThat(assertThrows(InvalidDataAccessApiUsageException.class, () -> patientRepository.existsById(null)).getMessage())
				.isEqualTo("The given id must not be null");

	}

}