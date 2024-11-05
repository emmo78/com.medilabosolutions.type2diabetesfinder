package com.medilabosolutions.type2diabetesfinder.patientservice.service;

import org.springframework.web.context.request.WebRequest;

/**
 * web request treatment
 *
 * @author Olivier MOREL
 *
 */
public interface RequestService {

    /**
     * To string all the parameters of a Web request
     *
     * @param request to String
     * @return String of parameters
     */
    String requestToString(WebRequest request);
}