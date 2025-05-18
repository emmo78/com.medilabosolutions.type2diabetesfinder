package com.medilabosolutions.type2diabetesfinder.noteservice.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * The StaticController class handles specific static routes for the application.
 * It provides methods to manage requests for the favicon and the Swagger UI.
 */
@Controller
public class StaticController {

    @GetMapping("favicon.ico")
    @ResponseBody
    void returnNoFavicon() {
    }

    @GetMapping("swagger-ui")
    @ResponseBody
    void returnSwaggerUI() {
    }

}
