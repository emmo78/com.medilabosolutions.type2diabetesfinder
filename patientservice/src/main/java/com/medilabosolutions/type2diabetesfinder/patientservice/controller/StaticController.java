package com.medilabosolutions.type2diabetesfinder.patientservice.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

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
