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
	public Patient createPatient(Patient patient) throws ResourceConflictException, UnexpectedRollbackException {
		Patient createdPatient = null;
		try {
			if(patient.getId()!= null) {
				throw new ResourceConflictException("Patient Id is not null");
			}
			// @Transactional is implemented by default on  repository methods
			// here it is alone
			createdPatient = patientRepository.save(patient);
		} catch(ResourceConflictException rce) {
			log.error("Patient={} : {}", patient, rce.toString());
			throw new ResourceConflictException(rce.getMessage());
		} catch(InvalidDataAccessApiUsageException | OptimisticLockingFailureException re) {
			log.error("Patient={} : {}", patient, re.toString());
			throw new UnexpectedRollbackException("Error while creating your profile");
		} catch(Exception e) {
			log.error("Patient={} : {}", patient, e.toString());
			throw new UnexpectedRollbackException("Error while creating your profile");
		}
		log.info("Patient={} created and persisted",
				createdPatient);
		return createdPatient;
	}

	@Override
	public Patient getPatient(Integer id) throws UnexpectedRollbackException {
		Patient patient = null;
		try {
			// @Transactional is implemented by default on  repository methods
			// here it is alone
			patient = patientRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Registrered not found"));
		} catch(ResourceNotFoundException | InvalidDataAccessApiUsageException re) {
			log.error(re.toString());
			throw new UnexpectedRollbackException("Error while getting your profile");
		} catch (Exception e) {
			log.error(e.toString());
			throw new UnexpectedRollbackException("Error while getting your profile");
		}
		log.info("Patient={} gotten",patient);
		return patient;
	}

	@Override
	public Patient updatePatient(Patient patient) throws UnexpectedRollbackException {
		Patient updatedPatient = null;
		try {
			// @Transactional is implemented by default on  repository methods
			// here it is alone
			patientRepository.findById(patient.getId()).orElseThrow(() -> new ResourceNotFoundException("Patient not found for update"));
			updatedPatient = patientRepository.save(patient);
		} catch(InvalidDataAccessApiUsageException | OptimisticLockingFailureException | ResourceNotFoundException re) {
			log.error("Patient={} : {}", patient, re.toString());
			throw new UnexpectedRollbackException("Error while updating your profile");
		} catch(Exception e) {
			log.error("Patient={} : {}", patient, e.toString());
			throw new UnexpectedRollbackException("Error while updating your profile");
		}
		log.info("Patient={} updated and persisted", updatedPatient);
		return updatedPatient;
	}

	@Override
	public void deletePatient(Integer id) throws UnexpectedRollbackException {
			try {
				Patient patient = patientRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Patient not found"));
				// @Transactional is implemented by default on  repository methods
				// here it is alone
				patientRepository.deleteById(id);
			} catch(InvalidDataAccessApiUsageException | ResourceNotFoundException re) {
				log.error(re.toString());
				throw new UnexpectedRollbackException("Error while removing your profile");
			} catch(Exception e) {
				log.error(e.toString());
				throw new UnexpectedRollbackException("Error while removing your profile");
			}
			log.info("Patient={} removed and deleted", id);
	}

	@Override
	public Page<Patient> getPatients(Pageable pageRequest) throws UnexpectedRollbackException{
		Page<Patient> pagePatient = null;
		try {
			//throws NullPointerException if pageRequest is null
			pagePatient = patientRepository.findAll(pageRequest);
		} catch(NullPointerException npe) {
			log.error(npe.toString());
			throw new UnexpectedRollbackException("Error while getting Registrants");
		} catch(Exception e) {
			log.error(e.toString());
			throw new UnexpectedRollbackException("Error while getting Registrants");
		}
		log.info("page registrants number : {} of {}",
				pagePatient.getNumber()+1,
				pagePatient.getTotalPages());
		return pagePatient;
	}
}