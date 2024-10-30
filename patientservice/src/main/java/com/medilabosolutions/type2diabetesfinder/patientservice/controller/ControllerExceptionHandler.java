package com.medilabosolutions.type2diabetesfinder.patientservice.controller;

import com.medilabosolutions.type2diabetesfinder.patientservice.error.ApiError;
import com.medilabosolutions.type2diabetesfinder.patientservice.service.RequestService;
import jakarta.validation.ConstraintViolationException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.coyote.BadRequestException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.context.request.WebRequest;

@ControllerAdvice
@Slf4j
@RequiredArgsConstructor
public class ControllerExceptionHandler {

    private final RequestService requestService;

    /**
     * Handle MethodArgumentNotValidException thrown by @Valid in @RequestBody
     * @param manvex the MethodArgumentNotValidException
     * @param request webrequest to log uri
     * @return return a Bad Request ResponseEntity with ApiError in body
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiError> methodArgumentNotValidException(MethodArgumentNotValidException manvex, WebRequest request) {
        ApiError error = new ApiError(HttpStatus.BAD_REQUEST, manvex);
        log.error("{} : {} : {}",
                requestService.requestToString(request),
                ((ServletWebRequest) request).getHttpMethod(),
                error.getMessage());
        return new ResponseEntity<>(error, error.getStatus());
    }

    /**
     * Handle ConstraintViolation thrown by constraint violation on path variable
     * @param cve the ConstraintViolationException
     * @param request webrequest to log uri
     * @return return a Bad Request ResponseEntity with ApiError in body
     */
    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ApiError> constraintViolationException(ConstraintViolationException cve, WebRequest request) {
        ApiError error = new ApiError(HttpStatus.BAD_REQUEST, cve);
        log.error("{} : {} : {}",
                requestService.requestToString(request),
                ((ServletWebRequest) request).getHttpMethod(),
                error.getMessage());
        return new ResponseEntity<>(error, error.getStatus());
    }

    /**
     * Handle UnexpectedRollBackException thrown by services
     * @param brex the UnexpectedRollBackException
     * @param request web request to log uri
     * @return return a Bad Request ResponseEntity with ApiError in body
     */
    @ExceptionHandler({NullPointerException.class, BadRequestException.class})
    public ResponseEntity<ApiError> badRequestException(Exception brex, WebRequest request) {
        ApiError error = new ApiError(HttpStatus.BAD_REQUEST, brex);
        log.error("{} : {} : {}",
                requestService.requestToString(request),
                ((ServletWebRequest) request).getHttpMethod(),
                error.getMessage());
        return new ResponseEntity<>(error, error.getStatus());
    }

    /**
     * Handle unexpected Exception : the exception message is logged and the message returned is "Internal Server Error"
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
