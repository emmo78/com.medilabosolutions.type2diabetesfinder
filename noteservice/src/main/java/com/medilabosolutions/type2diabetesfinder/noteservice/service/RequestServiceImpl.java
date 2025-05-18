package com.medilabosolutions.type2diabetesfinder.noteservice.service;

import org.springframework.stereotype.Service;
import org.springframework.web.context.request.WebRequest;

/**
 * Implementation of the RequestService interface for handling web request related operations.
 */
@Service
public class RequestServiceImpl implements RequestService {

    /**
     * {@inheritDoc}
     */
    @Override
    public String requestToString(WebRequest request) {
        //uri in StringBuffer
        StringBuffer parameters = new StringBuffer(request.getDescription(false) + "?");
        //p = parameter key of values v = String[]
        request.getParameterMap().forEach((p, v) -> {
            if (!p.equals("password")) {
                parameters.append(p + "=");
                int i = 0;
                while (i < (v.length - 1)) {
                    parameters.append(v[i] + ",");
                    i++;
                }
                parameters.append(v[i] + "&");
            }
        });
        int length = parameters.length();
        parameters.delete(length - 1, length);
        return parameters.toString();
    }
}