package com.pizzashop.controllers;

import com.pizzashop.dto.UserRegisterDTO;
import com.pizzashop.entities.RoleEnum;
import com.pizzashop.entities.User;
import com.pizzashop.services.UserRegistrationService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.propertyeditors.StringTrimmerEditor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/register")
public class RegistrationController {

    private final UserRegistrationService userService;

    @Autowired
    public RegistrationController(UserRegistrationService userService) {
        this.userService = userService;
    }

    // sets empty form fields to Null for validation purposes
    @InitBinder
    public void initBinder(WebDataBinder dataBinder) {

        StringTrimmerEditor stringTrimmerEditor = new StringTrimmerEditor(true);

        dataBinder.registerCustomEditor(String.class, stringTrimmerEditor);
    }

    @GetMapping("/showRegistrationForm")
    public String showRegistrationPage(Model theModel) {

        theModel.addAttribute("heading", "Pizza & Pasta");
        theModel.addAttribute("secondaryHeading", "RISTORANTE");
        theModel.addAttribute("webUser", new UserRegisterDTO());
        theModel.addAttribute("pageTitle", "Pizza & Pasta - Registration");
        theModel.addAttribute("additionalStyles", List.of("/styles/forms.css"));
        theModel.addAttribute("address", true);
        theModel.addAttribute("addType", "New User Registration");
        theModel.addAttribute("formAction", "/register/processRegistrationForm");

        return "auth/register";
    }

    @PostMapping("/processRegistrationForm")
    public String processRegistrationForm(
            @Valid @ModelAttribute("webUser") UserRegisterDTO theWebUser,
            BindingResult theBindingResult, Model theModel, RedirectAttributes redirectAttributes) {

        theModel.addAttribute("heading", "Pizza & Pasta");
        theModel.addAttribute("secondaryHeading", "RISTORANTE");
        theModel.addAttribute("additionalStyles", List.of("/styles/forms.css"));
        theModel.addAttribute("pageTitle", "Pizza & Pasta - Registration");
        theModel.addAttribute("address", true);

        if (theBindingResult.hasErrors()){
            theModel.addAttribute("registrationError", "You must correct the errors before proceeding");
            return "auth/register";
        }

        String userName = theWebUser.getUsername();
        Optional<User> existing = userService.findByUserName(userName);

        if (existing.isPresent()){
            theWebUser.setUsername(null);
            theWebUser.setPassword(null);
            theModel.addAttribute("webUser", theWebUser);
            theModel.addAttribute("registrationError", "Username: " + userName + " already exists.");

            return "auth/register";
        }

        redirectAttributes.addFlashAttribute("registered", "Registration Confirmed, Please Log In");
        // Customer is default role
        userService.save(theWebUser, RoleEnum.ROLE_CUSTOMER.name());

        return "redirect:/login";
    }
}
