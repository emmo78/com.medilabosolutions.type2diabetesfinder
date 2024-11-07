package com.medilabosolutions.type2diabetesfinder.patientservice.service;

import com.medilabosolutions.type2diabetesfinder.patientservice.model.Patient;
import com.medilabosolutions.type2diabetesfinder.patientservice.repository.PatientRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.coyote.BadRequestException;
import org.springframework.dao.InvalidDataAccessApiUsageException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.rest.webmvc.ResourceNotFoundException;
import org.springframework.stereotype.Service;

/**
 * Implementation of the PatientService interface for managing patient entities.
 * This service provides methods to retrieve, create, update, and delete patients.
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class PatientServiceImpl implements PatientService {

	private final PatientRepository patientRepository;

	/**
	 * Retrieves a page of patients based on the provided pagination information.
	 *
	 * @param pageRequest The pagination and sorting information for the request.
	 * @return A page of patients based on the given pagination information.
	 * @throws NullPointerException if the provided pageRequest is null.
	 */
	@Override
	public Page<Patient> getPatients(Pageable pageRequest) throws NullPointerException {
		//throws NullPointerException if pageRequest is null
		return patientRepository.findAll(pageRequest);
	}

	/**
	 * Retrieves a patient by their unique identifier.
	 *
	 * @param id the unique identifier of the patient to be retrieved
	 * @return the patient corresponding to the given identifier
	 * @throws ResourceNotFoundException if no patient is found with the given identifier
	 */
	@Override
	public Patient getPatient(Integer id) throws ResourceNotFoundException {
		// @Transactional is implemented by default on repository methods, here it is alone
		// Throw ResourceNotFoundException
		return patientRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Patient not found"));
	}

	/**
	 * Creates a new patient entity in the repository.
	 *
	 * @param patient the patient entity to be created
	 * @return the created patient entity
	 * @throws BadRequestException if the patient entity has a non-null ID
	 */
	@Override
	public Patient createPatient(Patient patient) throws BadRequestException {
		if(patient.getId() != null) {
			throw new BadRequestException("Patient to create has a not null Id !");
		}
		// @Transactional is implemented by default on repository methods, here it is alone
		return patientRepository.save(patient);
	}

	/**
	 * Updates an existing patient in the repository.
	 *
	 * @param patient the patient entity to be updated
	 * @return the updated patient entity
	 * @throws ResourceNotFoundException if no patient is found with the given identifier
	 * @throws InvalidDataAccessApiUsageException if the provided patient entity has a null identifier
	 */
	@Override
	public Patient updatePatient(Patient patient) throws ResourceNotFoundException, InvalidDataAccessApiUsageException {
		// Throw InvalidDataAccessApiUsageException if null id
		if (!patientRepository.existsById(patient.getId())) {
			throw new ResourceNotFoundException("Patient not found for update");
		}
		// @Transactional is implemented by default on repository methods here it is alone
		return patientRepository.save(patient);
	}

	/**
	 * Deletes a patient with the given ID. If the patient does not exist, the request is silently ignored.
	 *
	 * @param id the ID of the patient to be deleted. Must not be null.
	 * @throws InvalidDataAccessApiUsageException if the provided ID is null.
	 */
	@Override
	public void deletePatient(Integer id) throws InvalidDataAccessApiUsageException {
		// @Transactional is implemented by default on repository methods here it is alone
		// If the entity is not found in the persistence store it is silently ignored.
		// Throw InvalidDataAccessApiUsageException if null id
		patientRepository.deleteById(id);
	}
}