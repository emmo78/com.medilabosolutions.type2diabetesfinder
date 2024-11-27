package com.medilabosolutions.type2diabetesfinder.frontservice.service;

import com.medilabosolutions.type2diabetesfinder.patientservice.service.RequestService;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.WebRequest;

/**
 * Service implementation for handling web requests.
 * This class provides methods to process and convert web requests into a
 * string representation of their parameters, typically used for logging purposes.
 */
@Service
public class RequestServiceImpl implements RequestService {
    @Override
    public String requestToString(WebRequest request) {
        //uri in StringBuffer
        StringBuffer parameters = new StringBuffer(request.getDescription(false)+"?");
        //p = parameter key of values v = String[]
        request.getParameterMap().forEach((p,v) -> {
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
        parameters.delete(length-1, length);
        return parameters.toString();
    }
}
