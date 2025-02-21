package com.medilabosolutions.type2diabetesfinder.frontservice.controller;

import com.medilabosolutions.type2diabetesfinder.frontservice.service.RequestService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.context.request.WebRequest;

/**
 * ControlExceptionHandlerController class handles All Exception thrown by all classes annotated @Controller
 *
 * @author olivier morel
 */
@ControllerAdvice(annotations = Controller.class)
@Slf4j
@AllArgsConstructor
public class ControllerExceptionHandler {

    private final RequestService requestService;

    /**
     * Handle UnexpectedRollBackException thrown by services
     *
     * @param badRequestException the UnexpectedRollBackException
     * @param request             web request to log uri
     * @param model               part of Spring MVC, to contain data for the view (Thymeleaf)
     * @return the string "error" the view name for the view resolver
     */
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler({HttpClientErrorException.BadRequest.class, NumberFormatException.class})
    public String unexpectedRollbackException(RuntimeException badRequestException, WebRequest request, Model model) {
        String errorMessage = badRequestException.getMessage();
        log.error("{} : {} : {}",
                requestService.requestToString(request),
                ((ServletWebRequest) request).getHttpMethod(),
                errorMessage);
        model.addAttribute("errorMessage", "Bad request");
        return "error";
    }

    /**
     * Handle unexpected Exception : the exception message is logged and the message returned is "Internal Server Error"
     *
     * @param e       the Exception
     * @param request web request to log uri
     * @param model   part of Spring MVC, to contain data for the view (Thymeleaf)
     * @return the string "error" the view name for the view resolver
     */
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(Exception.class)
    public String unexpectedException(Exception e, WebRequest request, Model model) {
        log.error("{} : {} : {}",
                requestService.requestToString(request),
                ((ServletWebRequest) request).getHttpMethod(),
                e.getMessage());
        model.addAttribute("errorMessage", "Internal Server Error");
        return "error";
    }
}
