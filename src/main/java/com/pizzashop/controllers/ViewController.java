package com.pizzashop.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class ViewController {
    @GetMapping("/")
    public String index() {
        return "index";
    }

    @GetMapping("/order")
    public String order() {
        return "ordering/order";
    }

    @GetMapping("/system")
    public String system() {
        return "management/system";
    }
}
