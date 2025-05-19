package com.pizzashop.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/employees")
public class EmployeeDashboardController {

    @GetMapping("/showOrders")
    public String showOrders(Model model) {
        model.addAttribute("heading", "Current Orders");

        return "management/showNewOrders";
    }
}
