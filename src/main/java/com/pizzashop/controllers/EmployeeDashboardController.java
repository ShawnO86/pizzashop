package com.pizzashop.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/employees")
public class EmployeeDashboardController {

    // todo: add all pending orders on initial load to model, build initial Object in javascript
    //  --: will need to convert all order entities found to DTO? send with order ID to set order.isComplete to true after order built.
    @GetMapping("/showOrders")
    public String showOrders(Model model) {
        model.addAttribute("header", "Current Orders");
        return "management/showNewOrders";
    }
}
