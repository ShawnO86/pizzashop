package com.pizzashop.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/employees")
public class EmployeeDashboardController {

    // todo : MVC endpoint for recipe of selected menuItem and another for selected customPizza,
    //  param of menuItemId or customPizzaId - call data, send template with data

    @GetMapping("/showOrders")
    public String showOrders(Model model) {
        model.addAttribute("header", "Current Orders");

        return "management/showNewOrders";
    }

    @GetMapping("/menuItemRecipe")
    public String showMenuItemRecipe(Model model, @RequestParam(name = "menuItemId") int id) {
        // todo : set each recipe page to allow updating recipe by management only, some th:security


        return "management/showMenuItemRecipe";
    }

    @GetMapping("/customPizzaRecipe")
    public String showCustomPizzaRecipe(Model model, @RequestParam(name = "customPizzaId") int id) {



        return "management/showCustomPizzaRecipe";
    }

}
