package com.pizzashop.controllers;

import com.pizzashop.dao.UserDAO;
import com.pizzashop.dto.UserRegisterDTO;
import com.pizzashop.entities.Role;
import com.pizzashop.entities.RoleEnum;
import com.pizzashop.entities.User;
import com.pizzashop.entities.UserDetail;
import com.pizzashop.services.UserRegistrationService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.propertyeditors.StringTrimmerEditor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@SuppressWarnings("StringTemplateMigration")
@Controller
@RequestMapping("/system")
public class ManagementController {

    private final UserDAO userDAO;
    private final UserRegistrationService userService;

    @Autowired
    public ManagementController(UserDAO userDAO, UserRegistrationService userService) {
        this.userDAO = userDAO;
        this.userService = userService;
    }

    @InitBinder
    public void initBinder(WebDataBinder dataBinder) {
        StringTrimmerEditor stringTrimmerEditor = new StringTrimmerEditor(true);
        dataBinder.registerCustomEditor(String.class, stringTrimmerEditor);
    }

    @GetMapping("/userData")
    public String showUserDataPage(Model model) {
        model.addAttribute("heading", "User Data: All Users");
        List<User> users = userDAO.findAllFetchUserDetailsRoles();
        model.addAttribute("users", users);

        return "management/showUserData";
    }

    @GetMapping("/userDataByLastname")
    public String showUserDataByLastNamePage(@RequestParam("userLastName") String lastname, Model model) {
        model.addAttribute("heading", "User Data: " + lastname);
        List<User> users = userDAO.findAllByLastName(lastname);

        if (!users.isEmpty()) {
            model.addAttribute("users", users);
        } else {
            model.addAttribute("noUsers", "No users found by the last name: " + lastname);
        }

        return "management/showUserData";
    }

    @GetMapping("/showRegistrationForm")
    public String showRegistrationPage(Model model) {
        model.addAttribute("heading", "Registration Form");
        model.addAttribute("webUser", new UserRegisterDTO());

        return "auth/register";
    }


    @GetMapping("/showUpdateUserForm")
    public String showUpdateUserPage(@RequestParam("userId") int userId, Model model) {
        User user = userDAO.findByIdJoinFetchUserDetailsRoles(userId);

        if (user != null) {
            UserDetail userDetail = user.getUserDetail();
            UserRegisterDTO userDTO = new UserRegisterDTO(
                    user.getUsername(),
                    userDetail.getFirstName(),
                    userDetail.getLastName(),
                    userDetail.getEmail(),
                    userDetail.getPhone(),
                    userDetail.getAddress(),
                    userDetail.getCity(),
                    userDetail.getState()
            );
            userDTO.setPassword("");
            RoleEnum highestRole = this.getHighestRole(user.getRoles());

            model.addAttribute("heading", "Update User Form");
            model.addAttribute("webUser", userDTO);
            model.addAttribute("userId", userId);
            model.addAttribute("highestRole", highestRole);

            return "management/updateUser";
        } else {
            model.addAttribute("heading", "Register New User");
            model.addAttribute("webUser", new UserRegisterDTO());

            return "auth/register";
        }
    }

    @GetMapping("/deactivateUser")
    public String deleteUser(@RequestParam("userId") int userId) {
        userDAO.deactivateUser(userId);

        return "redirect:/system/userData";
    }

    @GetMapping("/activateUser")
    public String activateUser(@RequestParam("userId") int userId) {
        userDAO.activateUser(userId);

        return "redirect:/system/userData";
    }

    @PostMapping("/processManagementRegistrationForm")
    public String processManagementRegistrationForm(@Valid @ModelAttribute("webUser") UserRegisterDTO theWebUser, BindingResult bindingResult,
                                                    Model model, @RequestParam("userRole") String userRole) {

        String errMsg = "";

        if (bindingResult.hasErrors()){
            errMsg = "You must correct the errors before proceeding";
        }

        String userName = theWebUser.getUsername();
        Optional<User> existing = userService.findByUserName(userName);


        if (existing.isPresent()) {
            errMsg = "Username already exists.";
            theWebUser.setUsername(null);
        } else if (!userRole.isEmpty()) {
            //check if any role added that is not part of RoleEnum
            if (this.checkValidRole(userRole)) {
                errMsg = "Invalid user role: " + userRole;
            }
        }

        if (errMsg.isEmpty()) {
            userService.save(theWebUser, userRole);

            return "redirect:/system/userData";
        } else {
            theWebUser.setPassword(null);
            model.addAttribute("heading", "Register New User");
            model.addAttribute("webUser", theWebUser);
            model.addAttribute("registrationError", errMsg);

            return "auth/register";
        }
    }

    @PostMapping("/processUpdateUserForm")
    public String processUpdateUserForm(@Valid @ModelAttribute("webUser") UserRegisterDTO theWebUser, BindingResult bindingResult,
                                        Model model, @RequestParam("userRole") String userRole, @RequestParam("userId") int userId) {
        String errMsg = "";

        if (bindingResult.hasErrors()){
            errMsg = "You must correct the errors before proceeding";
        }


        if (errMsg.isEmpty()) {
            userService.update(theWebUser, userId, userRole);

            return "redirect:/system/userData";

        } else {
            theWebUser.setPassword(null);

            User user = userDAO.findById(userId);
            RoleEnum highestRole = this.getHighestRole(user.getRoles());
            model.addAttribute("registrationError", errMsg);
            model.addAttribute("heading", "Update User Form");
            model.addAttribute("webUser", theWebUser);
            model.addAttribute("userId", userId);
            model.addAttribute("highestRole", highestRole);

            return "management/updateUser";
        }

    }

    private RoleEnum getHighestRole(List<Role> roles) {
        RoleEnum highestRole = null;

        for (Role role : roles) {
            if (role.getRole() == RoleEnum.ROLE_MANAGER) {
                highestRole = RoleEnum.ROLE_MANAGER;
                break;
            } else if (role.getRole() == RoleEnum.ROLE_EMPLOYEE) {
                highestRole = RoleEnum.ROLE_EMPLOYEE;
            } else {
                highestRole = RoleEnum.ROLE_CUSTOMER;
            }
        }

        return highestRole;
    }

    private Boolean checkValidRole(String userRole) {
        //check if any role added that is not part of RoleEnum
        return userRole.equals(RoleEnum.ROLE_MANAGER.toString()) &&
                userRole.equals(RoleEnum.ROLE_EMPLOYEE.toString()) &&
                userRole.equals(RoleEnum.ROLE_CUSTOMER.toString());
    }

}
