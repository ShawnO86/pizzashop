package com.pizzashop.controllers;

import com.pizzashop.dao.MenuItemDAO;
import com.pizzashop.dto.OrderDTO;
import com.pizzashop.entities.MenuCategoryEnum;
import com.pizzashop.entities.MenuItem;
import com.pizzashop.services.OrderService;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.*;


@Controller
@RequestMapping("/order")
public class OrderController {

    private final MenuItemDAO menuItemDAO;
    private final OrderService orderService;

    @Autowired
    public OrderController(MenuItemDAO menuItemDAO, OrderService orderService) {
        this.menuItemDAO = menuItemDAO;
        this.orderService = orderService;
    }

    @GetMapping("/showMenu")
    public String showOrderForm(Model model) {
        model.addAttribute("order", new OrderDTO());
        model.addAttribute("heading", "Hungry? Create an order!");

        Map<String, List<MenuItem>> menuItemsByCategory = seperateMenuItemsByCategory(menuItemDAO.findAll());
        model.addAttribute("menuItemsByCategory", menuItemsByCategory);

        return "ordering/orderForm";
    }


    @PostMapping("/processOrder")
    public String processOrder(@Valid @ModelAttribute("order") OrderDTO orderDTO, Model model,
                               @RequestParam("menuItemsNamesList") String[] menuItemsNamesList,
                               @RequestParam("menuItemsAmountsList") Integer[] menuItemsAmountsList) {


        // ToDo: check for item amounts over amount possible based on inventory used for them

        // checks for difference in array's lengths or if there are no items added.
        String errMsg = "";
        if (menuItemsNamesList.length != menuItemsAmountsList.length) {
            errMsg = "Menu items and quantity mismatch!";
        } else if (menuItemsNamesList.length == 0) {
            errMsg = "No menu items added!";
        }

        if (errMsg.isEmpty()) {
            Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            String username = ((UserDetails) principal).getUsername();

            orderService.submitOrderForFulfillment(orderDTO, username);
            return "ordering/order-confirmation";
        } else {
            Map<String, List<MenuItem>> menuItemsByCategory = seperateMenuItemsByCategory(menuItemDAO.findAll());
            model.addAttribute("menuItemsByCategory", menuItemsByCategory);
            model.addAttribute("orderError", errMsg);
            model.addAttribute("order", orderDTO);
            model.addAttribute("heading", "Hungry? Create an order!");
            return "ordering/orderForm";
        }
    }


    private Map<String, List<MenuItem>> seperateMenuItemsByCategory(List<MenuItem> menuItems) {
        Map<String, List<MenuItem>> menuItemsByCategory = new HashMap<>();

        for (MenuCategoryEnum menuCategory : MenuCategoryEnum.values()) {
            menuItemsByCategory.put(menuCategory.name(), new ArrayList<>());
        }

        for (MenuItem menuItem : menuItems) {
            menuItemsByCategory.get(menuItem.getMenuCategory().name()).add(menuItem);
        }

        return menuItemsByCategory;
    }
}
