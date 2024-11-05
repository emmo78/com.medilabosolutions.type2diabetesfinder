package com.medilabosolutions.type2diabetesfinder.patientservice.controller;

import com.medilabosolutions.type2diabetesfinder.patientservice.error.ApiError;
import com.medilabosolutions.type2diabetesfinder.patientservice.service.RequestService;
import jakarta.validation.ConstraintViolationException;
import lombok.RequiredArgsConstructor;
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

@ControllerAdvice
@Slf4j
@RequiredArgsConstructor
public class ControllerExceptionHandler {

    private final RequestService requestService;

    /**
     * Handle all kind of bad request Exception thrown by services :
     * the exception message is logged and the message returned is "Bad request"
     * @param brex
     * @param request web request to log uri
     * @return return a Bad Request ResponseEntity with ApiError in body
     */
    @ExceptionHandler({MethodArgumentTypeMismatchException.class, InvalidDataAccessApiUsageException.class, MethodArgumentNotValidException.class, ConstraintViolationException.class, ResourceNotFoundException.class, NullPointerException.class, BadRequestException.class})
    public ResponseEntity<ApiError> badRequestException(Exception brex, WebRequest request) {
        log.error("{} : {} : {}",
                requestService.requestToString(request),
                ((ServletWebRequest) request).getHttpMethod(),
                brex.getMessage());
        ApiError error = new ApiError(HttpStatus.BAD_REQUEST, "Bad request");
        return new ResponseEntity<>(error, error.getStatus());
    }

    /**
     * Handle other and unexpected Exception : the exception message is logged and the message returned is "Internal Server Error"
     * @param e the Exception
     * @param request web request to log uri
     * @return the string "error" the view name for the view resolver
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
