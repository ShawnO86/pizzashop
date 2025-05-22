package com.pizzashop.controllers;

import com.pizzashop.dto.SalesReportDateDTO;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class ViewController {
    //base views
    @GetMapping("/")
    public String index(Model model) {
        model.addAttribute("heading", "Pizza Shop");
        return "index";
    }

    @GetMapping("/system")
    public String showManagementPage(Model model) {
        model.addAttribute("heading", "System Administration");
        model.addAttribute("salesReportDates", new SalesReportDateDTO());
        return "management/system";
    }


}
