package com.medilabosolutions.type2diabetesfinder.patientservice.service;

import com.medilabosolutions.type2diabetesfinder.patientservice.exception.ResourceConflictException;
import com.medilabosolutions.type2diabetesfinder.patientservice.model.Patient;
import com.medilabosolutions.type2diabetesfinder.patientservice.repository.PatientRepository;
import org.springframework.dao.InvalidDataAccessApiUsageException;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.rest.webmvc.ResourceNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.UnexpectedRollbackException;


import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@AllArgsConstructor
public class PatientServiceImpl implements PatientService {

	private final PatientRepository patientRepository;

	@Override
	public Patient createPatient(Patient patient) throws ResourceConflictException {
		if(patient.getId()!= null) {
			throw new ResourceConflictException("Patient Id is not null");
		}
		// @Transactional is implemented by default on  repository methods, here it is alone
		// Throw InvalidDataAccessApiUsageException | OptimisticLockingFailureException
		Patient createdPatient = patientRepository.save(patient);
		log.info("Patient={} created and persisted",
				createdPatient);
		return createdPatient;
	}

	@Override
	public Patient getPatient(Integer id) throws ResourceNotFoundException {
		// @Transactional is implemented by default on  repository methods, here it is alone
		// Throw ResourceNotFoundException | InvalidDataAccessApiUsageException
		Patient	patient = patientRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Patient not found"));
		log.info("Patient={} gotten",patient);
		return patient;
	}

	@Override
	public Patient updatePatient(Patient patient) throws ResourceNotFoundException {
		// Throw InvalidDataAccessApiUsageException if null id
		if (!patientRepository.existsById(patient.getId())) {
			throw new ResourceNotFoundException("Patient not found for update");
		}
		// @Transactional is implemented by default on  repository methods here it is alone
		// Throw InvalidDataAccessApiUsageException | OptimisticLockingFailureException
		Patient updatedPatient = patientRepository.save(patient);
		log.info("Patient={} updated and persisted", updatedPatient);
		return updatedPatient;
	}

	@Override
	public void deletePatient(Integer id) {
		// @Transactional is implemented by default on  repository methods here it is alone
		// If the entity is not found in the persistence store it is silently ignored.
		// Throw InvalidDataAccessApiUsageException
		patientRepository.deleteById(id);
		log.info("Patient={} removed and deleted", id);
	}

	@Override
	public Page<Patient> getPatients(Pageable pageRequest) throws NullPointerException {
 		//throws NullPointerException if pageRequest is null
		Page<Patient> pagePatient = patientRepository.findAll(pageRequest);
		log.info("page registrants number : {} of {}",
				pagePatient.getNumber()+1,
				pagePatient.getTotalPages());
		return pagePatient;
	}
}