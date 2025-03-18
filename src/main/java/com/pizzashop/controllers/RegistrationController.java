package com.pizzashop.controllers;

import com.pizzashop.dto.UserRegisterDTO;
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

        theModel.addAttribute("webUser", new UserRegisterDTO());

        return "auth/register";
    }

    @PostMapping("/processRegistrationForm")
    public String processRegistrationForm(
            @Valid @ModelAttribute("webUser") UserRegisterDTO theWebUser,
            BindingResult theBindingResult, Model theModel) {

        String userName = theWebUser.getUsername();
        System.out.println("Processing user: " + theWebUser);

        // form validation
        if (theBindingResult.hasErrors()){
            theModel.addAttribute("registrationError", "You must correct the errors before proceeding");
            return "auth/register";
        }

        // check the database if username already used
        Optional<User> existing = userService.findByUserName(userName);

        if (existing.isPresent()){
            // instead of creating a new blank 'WebUser DTO', just null out the username and password and
            // provide already populated details
            theWebUser.setUsername(null);
            theWebUser.setPassword(null);
            theModel.addAttribute("webUser", theWebUser);
            theModel.addAttribute("registrationError", "Username already exists.");

            return "auth/register";
        }

        // create user account and store in the database
        // I will have to populate userDetails
        userService.save(theWebUser);

        return "auth/registration-confirmation";
    }
}
