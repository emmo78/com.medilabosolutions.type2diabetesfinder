package com.medilabosolutions.type2diabetesfinder.noteservice.exception;

/**
 * Exception thrown to indicate that a resource not found has occurred.
 * Extends {@link RuntimeException}.
 */
public class RessourceNotFoundException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    /**
     * Constructs a new ResourceNotFoundException with the specified detail message.
     *
     * @param message the detail message explaining the reason for the exception.
     */
    public RessourceNotFoundException(String message) {
        super(message);
    }
}
