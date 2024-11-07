package com.medilabosolutions.type2diabetesfinder.patientservice.error;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.Setter;
import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;

/**
 * The ApiError class represents an error response for an API. It contains details about the
 * HTTP status, a timestamp of when the error occurred, and an error message.
 *
 * This class is typically used in exception handling within a REST API to construct a well-defined
 * error response that can be returned to clients when an exception is thrown.
 *
 * The class provides two constructors:
 * <ul>
 * <li>ApiError(HttpStatus status, Throwable ex) - Constructs an ApiError instance using the given HTTP status and exception.</li>
 * <li>ApiError(HttpStatus status, String message) - Constructs an ApiError instance using the given HTTP status and custom error message.</li>
 * </ul>
 */
@Getter
@Setter
public class ApiError {

    private HttpStatus status;
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private LocalDateTime timestamp;
    private String message;

    /**
     * Initializes a new instance of the {@code ApiError} class with the current timestamp.
     *
     * This constructor is private and used internally to set the timestamp when an instance
     * of the {@code ApiError} class is created using one of the public constructors.
     */
    private ApiError() {
        timestamp = LocalDateTime.now();
    }

    /**
     * Constructs an {@code ApiError} instance using the given HTTP status and exception.
     *
     * @param status the HTTP status associated with the error
     * @param ex the exception from which the error message will be extracted
     */
    public ApiError(HttpStatus status, Throwable ex) {
        this();
        this.status = status;
        this.message = ex.getMessage();
    }

    /**
     * Constructs an {@code ApiError} instance using the given HTTP status and custom error message.
     *
     * @param status the HTTP status associated with the error
     * @param message the custom error message
     */
    public ApiError(HttpStatus status, String message) {
        this();
        this.status = status;
        this.message = message;
    }
}
