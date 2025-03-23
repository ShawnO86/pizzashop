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

        // check for duplicates in names list and produce error msg if so
        // check for difference in each array lengths
        // check for item amounts over amount possible based on inventory

        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String username = ((UserDetails) principal).getUsername();

        System.out.println("Item Names :" + Arrays.toString(menuItemsNamesList));
        System.out.println("Item Amounts :" + Arrays.toString(menuItemsAmountsList));

        orderService.submitOrderForFulfillment(orderDTO, username);
        return "ordering/order-confirmation";
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
