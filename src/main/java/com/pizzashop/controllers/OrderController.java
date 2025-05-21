package com.pizzashop.controllers;

import com.pizzashop.dao.IngredientDAO;
import com.pizzashop.dao.MenuItemDAO;
import com.pizzashop.dao.OrderDAO;
import com.pizzashop.dto.*;
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

// todo : add viewport to all html ...

@Controller
@RequestMapping("/order")
public class OrderController {

    private final MenuItemDAO menuItemDAO;
    private final IngredientDAO ingredientDAO;
    private final OrderService orderService;
    private final OrderDAO orderDAO;
    private final OrderNotificationController orderNotificationController;

    @Autowired
    public OrderController(MenuItemDAO menuItemDAO, IngredientDAO ingredientDAO, OrderService orderService, OrderDAO orderDAO,
                           OrderNotificationController orderNotificationController) {
        this.menuItemDAO = menuItemDAO;
        this.ingredientDAO = ingredientDAO;
        this.orderService = orderService;
        this.orderDAO = orderDAO;
        this.orderNotificationController = orderNotificationController;
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
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String username = ((UserDetails) principal).getUsername();

        // todo: uncomment existing check after testing
        //Order existingOrder = orderDAO.findByUsername(username);

        // {availabilityErrors, []}, {priceErrors, []}
        Map<String, List<String>> validationResponse;

        if (orderDTO.getCustomPizzaList() == null && orderDTO.getMenuItemList() == null) {
            model.addAttribute("cartError", "Nothing in cart!");
            model.addAttribute("order", orderDTO);
            return showOrderForm(model);
        }/* else if (existingOrder != null) {
            model.addAttribute("cartError", "Your current order is being processed!");
            model.addAttribute("order", new OrderDTO());
            return showOrderForm(model);
        }*/ else {
            System.out.println("* validation *");
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

        Order order = orderService.submitOrder(orderDTO, username);

        if (order == null) {
            model.addAttribute("cartError", "User not found!");
            model.addAttribute("order", orderDTO);
            return showOrderForm(model);
        }

        orderDTO.setOrderID(order.getId());
        System.out.println("*** Notify of new order...");
        orderNotificationController.notifyNewOrder(orderService.convertOrderToDTO(order, true));
        redirectAttributes.addFlashAttribute("order", orderDTO);

        return "redirect:/order/confirmOrder?orderId=" + order.getId();
    }

    @GetMapping("/confirmOrder")
    public String showConfirmation(Model model, @RequestParam("orderId") int orderId) {
        if (!model.containsAttribute("order")) {
            System.out.println("* confirmation *");
            Order confirmedOrder = orderDAO.findById(orderId);
            if (confirmedOrder != null) {
                OrderDTO confirmedOrderDTO = orderService.convertOrderToDTO(confirmedOrder, false);
                model.addAttribute("heading", "Your order is confirmed!");
                model.addAttribute("order", confirmedOrderDTO);
            } else {
                model.addAttribute("orderError", "Order not found!");
            }
        }

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
