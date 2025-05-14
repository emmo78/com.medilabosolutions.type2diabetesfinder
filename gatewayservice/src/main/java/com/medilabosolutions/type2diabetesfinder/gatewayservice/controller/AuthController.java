package com.medilabosolutions.type2diabetesfinder.gatewayservice.controller;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.security.Principal;

@Controller
public class AuthController {

    @GetMapping("/login")
    public String login(Principal user) {
        if (isAuthenticated(user)) {
            return "redirect:/front/home";
        }
        return "login";
    }

    private boolean isAuthenticated(Principal user) {
        UsernamePasswordAuthenticationToken token = (UsernamePasswordAuthenticationToken) user;
        if (token != null) {
            if (token.isAuthenticated()) {
                return true;
            }
        }
        return false;
    }
}
