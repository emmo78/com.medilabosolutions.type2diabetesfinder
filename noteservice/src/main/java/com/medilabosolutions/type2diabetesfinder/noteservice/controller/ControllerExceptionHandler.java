package com.medilabosolutions.type2diabetesfinder.noteservice.controller;

import com.medilabosolutions.type2diabetesfinder.noteservice.error.ApiError;
import com.medilabosolutions.type2diabetesfinder.noteservice.service.RequestService;
import jakarta.validation.ConstraintViolationException;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.coyote.BadRequestException;
import org.springframework.dao.InvalidDataAccessApiUsageException;
import org.springframework.data.rest.webmvc.ResourceNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

/**
 * ControlExceptionHandlerController class handles All Exception thrown by all classes annotated @Controller
 */
@ControllerAdvice
@Slf4j
@AllArgsConstructor
public class ControllerExceptionHandler {

    private final RequestService requestService;

    /**
     * Handles exceptions related to bad requests and converts them into a standardized {@link ApiError} response.
     * This method is specifically designed to handle exceptions such as {@code MethodArgumentTypeMismatchException},
     * {@code InvalidDataAccessApiUsageException}, {@code MethodArgumentNotValidException}, {@code ConstraintViolationException},
     * {@code ResourceNotFoundException}, {@code NullPointerException}, and {@code BadRequestException}.
     *
     * @param brex    the exception that was thrown
     * @param request the web request during which the exception was raised
     * @return a {@code ResponseEntity} containing the {@code ApiError} with an HTTP status of {@code BAD_REQUEST}
     */
    @ExceptionHandler({MethodArgumentTypeMismatchException.class
            , IllegalArgumentException.class
            , MethodArgumentNotValidException.class
            , ConstraintViolationException.class
            , ResourceNotFoundException.class
            , BadRequestException.class})
    public ResponseEntity<ApiError> badRequestException(Exception brex, WebRequest request) {
        log.error("{} : {} : {}",
                requestService.requestToString(request),
                ((ServletWebRequest) request).getHttpMethod(),
                brex.getMessage());
        ApiError error = new ApiError(HttpStatus.BAD_REQUEST, "Bad request");
        return new ResponseEntity<>(error, error.getStatus());
    }

    /**
     * Handle unexpected Exception : the exception message is logged and the message returned is "Internal Server Error"
     *
     * @param e       the Exception
     * @param request web request to log uri
     * @return a {@code ResponseEntity} containing the {@code ApiError} with an HTTP status of {@code INTERNAL_SERVER_ERROR}
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiError> unexpectedException(Exception e, WebRequest request) {
        log.error("{} : {} : {}",
                requestService.requestToString(request),
                ((ServletWebRequest) request).getHttpMethod(),
                e.getMessage());
        ApiError error = new ApiError(HttpStatus.INTERNAL_SERVER_ERROR, "Internal Server Error");
        return new ResponseEntity<>(error, error.getStatus());
    }
}