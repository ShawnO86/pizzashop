package com.pizzashop.controllers;

import com.pizzashop.dao.OrderDAO;
import com.pizzashop.dao.UserDAO;
import com.pizzashop.dto.OrderDTO;
import com.pizzashop.dto.SalesReportInfoDTO;
import com.pizzashop.dto.UserRegisterDTO;
import com.pizzashop.entities.*;
import com.pizzashop.services.OrderService;
import com.pizzashop.services.UserRegistrationService;
import jakarta.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.propertyeditors.StringTrimmerEditor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

@Controller
@RequestMapping("/system")
public class ManagementController {

    private final UserDAO userDAO;
    private final UserRegistrationService userService;
    private final OrderDAO orderDAO;
    private final OrderService orderService;

    @Autowired
    public ManagementController(UserDAO userDAO, UserRegistrationService userService, OrderDAO orderDAO, OrderService orderService) {
        this.userDAO = userDAO;
        this.userService = userService;
        this.orderDAO = orderDAO;
        this.orderService = orderService;
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

    @GetMapping("/showSalesReportForm")
    public String showSalesReportForm(Model model) {
        List<String> employees =  userDAO.findAllEmployeeUsernames();

        model.addAttribute("employees", employees);
        model.addAttribute("salesReportInfo", new SalesReportInfoDTO());
        model.addAttribute("heading", "Sales Report");
        return "management/showSalesReport";
    }

    // todo: build page with orders made during specified timeframe and all ingredients used for each of those orders and total ingredients used
    @PostMapping("/showSalesReport")
    public String showSalesReport(@Valid @ModelAttribute("salesReportInfo") SalesReportInfoDTO salesReportInfo,
                                  BindingResult bindingResult,
                                  Model model) {

        List<String> employees =  userDAO.findAllEmployeeUsernames();
        model.addAttribute("employees", employees);
        model.addAttribute("heading", "Sales Report");

        if (bindingResult.hasErrors()) {
            return "management/showSalesReport";
        }

        LocalDate startDate = salesReportInfo.getStartDate();
        LocalDate endDate = salesReportInfo.getEndDate();
        if (startDate != null && endDate != null && startDate.isAfter(endDate)) {
            bindingResult.rejectValue("endDate", "date.range.invalid", "End date must be after start date.");
            return "management/showSalesReport";
        }

        //  todo : build list of orderDTOs and count ingredients total
        String employeeUsername = salesReportInfo.getEmployeeUsername();
        List<Order> orders;

        if (Objects.equals(employeeUsername, "all")) {
            orders = orderDAO.findAllByDateRange(startDate, endDate);
        } else {
            orders = orderDAO.findAllFulfilledByIdInDateRange(startDate, endDate, employeeUsername);
        }

        List<OrderDTO> orderDTOList = orderService.buildOrderDTOlist(orders);
        Map<String, Integer> inventoryUsage = orderService.countTotalInventoryUsage(orderDTOList);

        System.out.println("*** inventory usage: " + inventoryUsage);
        model.addAttribute("report", orderDTOList);
        model.addAttribute("inventoryUsage", inventoryUsage);

        return "management/showSalesReport";
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
