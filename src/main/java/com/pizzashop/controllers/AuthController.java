package com.pizzashop.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.Collections;
import java.util.List;

@Controller
public class AuthController {

    @GetMapping("/login")
    public String showLoginPage(Model model) {
        model.addAttribute("heading", "Pizza & Pasta");
        model.addAttribute("secondaryHeading", "RISTORANTE");
        model.addAttribute("pageTitle", "Pizza & Pasta - Login");
        model.addAttribute("additionalStyles", List.of("/styles/forms.css"));
        model.addAttribute("address", true);

        return "auth/login";
    }

    @GetMapping("/access-denied")
    public String showAccessDeniedPage(Model model) {
        model.addAttribute("heading", "Pizza & Pasta");
        model.addAttribute("secondaryHeading", "RISTORANTE");
        model.addAttribute("pageTitle", "Access denied");
        model.addAttribute("additionalStyles", Collections.emptyList());
        model.addAttribute("address", true);

        return "auth/access-denied";
    }
}
