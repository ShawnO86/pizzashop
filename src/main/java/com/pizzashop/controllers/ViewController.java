package com.pizzashop.controllers;

import com.pizzashop.services.MenuItemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.Collections;

@Controller
public class ViewController {

    MenuItemService menuItemService;

    @Autowired
    public ViewController(MenuItemService menuItemService) {
        this.menuItemService = menuItemService;
    }

    //base views
    @GetMapping("/")
    public String index(Model model) {
        model.addAttribute("heading", "Pizza & Pasta");
        model.addAttribute("secondaryHeading", "RISTORANTE");
        model.addAttribute("pageTitle", "Pizza & Pasta - Home");
        model.addAttribute("additionalStyles", Collections.emptyList());
        model.addAttribute("address", true);

        model.addAttribute("specials", menuItemService.findRandomMenuItems());

        return "index";
    }

    @GetMapping("/system")
    public String showManagementPage(Model model) {
        model.addAttribute("heading", "System Administration");
        model.addAttribute("secondaryHeading", "");
        model.addAttribute("pageTitle", "System Administration");
        model.addAttribute("additionalStyles", Collections.emptyList());
        return "management/system";
    }


}
