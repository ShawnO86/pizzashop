package com.pizzashop.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class ViewController {
    //base views
    @GetMapping("/")
    public String index() {
        return "index";
    }

    @GetMapping("/system")
    public String showManagementPage() {
        return "management/system";
    }

}
