package com.pizzashop.controllers;

import com.pizzashop.dao.MenuItemDAO;
import com.pizzashop.dto.OrderDTO;
import com.pizzashop.services.OrderService;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/order")
public class OrderController {

    MenuItemDAO menuItemDAO;
    OrderService orderService;

    @Autowired
    public OrderController(MenuItemDAO menuItemDAO, OrderService orderService) {
        this.menuItemDAO = menuItemDAO;
        this.orderService = orderService;
    }

    @GetMapping("/addOrder")
    public String showOrderForm(Model model) {
        model.addAttribute("order", new OrderDTO());
        return "ordering/menuForm";
    }

    @GetMapping("/updateOrder")
    public String showOrderForm(@RequestParam("order")OrderDTO orderDTO, Model model) {
        model.addAttribute("order", orderDTO);
        return "ordering/menuForm";
    }

    @PostMapping("/processOrder")
    public String processOrder(@Valid @ModelAttribute("order") OrderDTO orderDTO, Model model,
                               BindingResult bindingResult) {

        if (bindingResult.hasErrors()) {
            model.addAttribute("order", orderDTO);
            return "ordering/menuForm";
        }

        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String username = ((UserDetails) principal).getUsername();

        orderService.addOrderToDB(orderDTO, username);

        return "ordering/order-confirmation";
    }

}
