package com.pizzashop.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class AuthController {

    @GetMapping("/loginPage")
    public String showLoginPage() {
        return "auth/login";
    }

    @GetMapping("/access-denied")
    public String showAccessDeniedPage() {
        return "auth/access-denied";
    }
}
