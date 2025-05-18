package com.medilabosolutions.type2diabetesfinder.noteservice.service;

import org.springframework.web.context.request.WebRequest;

/**
 * RequestService is an interface that defines operations for handling web requests.
 */
public interface RequestService {

    /**
     * Converts a WebRequest to a string representation.
     *
     * @param request the WebRequest to convert
     * @return a string representation of the WebRequest
     */
    String requestToString(WebRequest request);
}