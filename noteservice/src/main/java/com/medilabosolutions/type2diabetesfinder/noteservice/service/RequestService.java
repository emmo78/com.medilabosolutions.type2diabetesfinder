package com.medilabosolutions.type2diabetesfinder.noteservice.service;

import org.springframework.stereotype.Service;
import org.springframework.web.context.request.WebRequest;

/**
 * Service class for handling web request related operations.
 */
@Service
public class RequestService {

    /**
     * Converts a WebRequest to a string representation.
     *
     * @param request the WebRequest to convert
     * @return a string representation of the WebRequest
     */
    public String requestToString(WebRequest request) {
        return request.getDescription(true);
    }
}