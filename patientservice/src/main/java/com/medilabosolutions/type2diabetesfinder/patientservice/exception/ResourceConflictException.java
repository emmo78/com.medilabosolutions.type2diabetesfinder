package com.medilabosolutions.type2diabetesfinder.patientservice.exception;

/**
 * Exception thrown to indicate that a resource conflict has occurred.
 * This typically happens when there is an attempt to create or update
 * a resource in a way that violates uniqueness or other constraints.
 *
 * Extends {@link RuntimeException}.
  */
public class ResourceConflictException extends RuntimeException {
	private static final long serialVersionUID = 1L;

	/**
	 * Constructs a new ResourceConflictException with the specified detail message.
	 *
	 * @param message the detail message explaining the reason for the exception.
	 */
	public ResourceConflictException(String message) {
		super(message);
	}
}
