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

@Service
@Slf4j
@RequiredArgsConstructor
public class PatientServiceImpl implements PatientService {

	private final PatientRepository patientRepository;

	@Override
	public Page<Patient> getPatients(Pageable pageRequest) throws NullPointerException {
		//throws NullPointerException if pageRequest is null
		return patientRepository.findAll(pageRequest);
	}

	@Override
	public Patient getPatient(Integer id) throws ResourceNotFoundException {
		// @Transactional is implemented by default on repository methods, here it is alone
		// Throw ResourceNotFoundException
		return patientRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Patient not found"));
	}

	@Override
	public Patient createPatient(Patient patient) throws BadRequestException {
		if(patient.getId() != null) {
			throw new BadRequestException("Patient to create has a not null Id !");
		}
		// @Transactional is implemented by default on repository methods, here it is alone
		return patientRepository.save(patient);
	}

	@Override
	public Patient updatePatient(Patient patient) throws ResourceNotFoundException, InvalidDataAccessApiUsageException {
		// Throw InvalidDataAccessApiUsageException if null id
		if (!patientRepository.existsById(patient.getId())) {
			throw new ResourceNotFoundException("Patient not found for update");
		}
		// @Transactional is implemented by default on repository methods here it is alone
		return patientRepository.save(patient);
	}

	@Override
	public void deletePatient(Integer id) throws InvalidDataAccessApiUsageException {
		// @Transactional is implemented by default on repository methods here it is alone
		// If the entity is not found in the persistence store it is silently ignored.
		// Throw InvalidDataAccessApiUsageException if null id
		patientRepository.deleteById(id);
	}
}