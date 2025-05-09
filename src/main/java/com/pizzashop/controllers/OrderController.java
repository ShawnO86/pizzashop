package com.pizzashop.controllers;

import com.pizzashop.dao.IngredientDAO;
import com.pizzashop.dao.MenuItemDAO;
import com.pizzashop.dao.OrderDAO;
import com.pizzashop.dto.OrderDTO;
import com.pizzashop.entities.*;
import com.pizzashop.services.OrderService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.*;

@Controller
@RequestMapping("/order")
public class OrderController {

    private final MenuItemDAO menuItemDAO;
    private final IngredientDAO ingredientDAO;
    private final OrderService orderService;
    private final OrderDAO orderDAO;

    @Autowired
    public OrderController(MenuItemDAO menuItemDAO, IngredientDAO ingredientDAO, OrderService orderService, OrderDAO orderDAO) {
        this.menuItemDAO = menuItemDAO;
        this.ingredientDAO = ingredientDAO;
        this.orderService = orderService;
        this.orderDAO = orderDAO;
    }

    @GetMapping("/showMenu")
    public String showOrderForm(Model model) {
        Map<String, List<MenuItem>> menuItemsByCategory = separateMenuItemsByCategory(menuItemDAO.findAllAvailable());
        List<Ingredient> pizzaToppings = ingredientDAO.findAllPizzaToppings();

        model.addAttribute("heading", "Hungry? Create an order!");
        model.addAttribute("menuItemsByCategory", menuItemsByCategory);
        model.addAttribute("pizzaSizes", PizzaSizeEnum.values());
        model.addAttribute("pizzaToppings", pizzaToppings);

        if (!model.containsAttribute("order")) {
            model.addAttribute("order", new OrderDTO());
        }

        return "ordering/orderForm";
    }

    @PostMapping("/processOrder")
    public String processOrder(Model model, @ModelAttribute("order") OrderDTO orderDTO, RedirectAttributes redirectAttributes) {

        // todo : validate orderDTO, enough ingredients, price -- done
        //  -- DO - send to confirmation page with receipt and render valid orderDTO OR
        //  -- output errors in order form and send orderDTO back

        System.out.println("orderDTO --> \n" + orderDTO);

        // {availabilityErrors, []}, {priceErrors, []}
        Map<String, List<String>> validationResponse;

        if (orderDTO.getCustomPizzaList() == null && orderDTO.getMenuItemList() == null) {
            model.addAttribute("cartError", "Nothing in cart!");
            model.addAttribute("order", orderDTO);
            return showOrderForm(model);
        } else {
            validationResponse = orderService.submitOrderForValidation(orderDTO);
        }

        if ((validationResponse.get("availabilityErrors") != null && !validationResponse.get("availabilityErrors").isEmpty()) ||
                (validationResponse.get("priceErrors") != null && !validationResponse.get("priceErrors").isEmpty())) {

            System.out.println("validationResponse.." + validationResponse.get("availabilityErrors") + "\n" + validationResponse.get("priceErrors"));
            if (!validationResponse.get("availabilityErrors").isEmpty()) {
                model.addAttribute("availabilityErrors", validationResponse.get("availabilityErrors"));
            }
            if (!validationResponse.get("priceErrors").isEmpty()) {
                model.addAttribute("priceErrors", validationResponse.get("priceErrors"));
            }

            model.addAttribute("order", orderDTO);

            return showOrderForm(model);
        }

        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String username = ((UserDetails) principal).getUsername();

        int orderId = orderService.submitOrder(orderDTO, username);

        if (orderId == 0) {
            model.addAttribute("cartError", "User not found!");
            model.addAttribute("order", orderDTO);
            return showOrderForm(model);
        }

        redirectAttributes.addFlashAttribute("order", orderDTO);
        return "redirect:/order/confirmOrder?orderId=" + orderId;
    }

    @GetMapping("/confirmOrder")
    public String showConfirmation(Model model, @RequestParam("orderId") int orderId) {

        model.addAttribute("heading", "Your order is confirmed!");
        model.addAttribute("orderId", orderId);

        return "ordering/order-confirmation";
    }

    private Map<String, List<MenuItem>> separateMenuItemsByCategory(List<MenuItem> menuItems) {
        Map<String, List<MenuItem>> menuItemsByCategory = new LinkedHashMap<>();

        for (MenuCategoryEnum menuCategory : MenuCategoryEnum.values()) {
            menuItemsByCategory.put(menuCategory.name(), new ArrayList<>());
        }

        for (MenuItem menuItem : menuItems) {
            menuItemsByCategory.get(menuItem.getMenuCategory().name()).add(menuItem);
        }

        return menuItemsByCategory;
    }
}
