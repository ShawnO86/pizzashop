package com.pizzashop.controllers;

import com.pizzashop.dao.IngredientDAO;
import com.pizzashop.dao.MenuItemDAO;
import com.pizzashop.dto.OrderDTO;
import com.pizzashop.entities.Ingredient;
import com.pizzashop.entities.MenuCategoryEnum;
import com.pizzashop.entities.MenuItem;
import com.pizzashop.entities.PizzaSizeEnum;
import com.pizzashop.services.OrderService;

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
    private final IngredientDAO ingredientDAO;
    private final OrderService orderService;

    @Autowired
    public OrderController(MenuItemDAO menuItemDAO, IngredientDAO ingredientDAO, OrderService orderService) {
        this.menuItemDAO = menuItemDAO;
        this.ingredientDAO = ingredientDAO;
        this.orderService = orderService;
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
    public String processOrder(Model model, @ModelAttribute("order") OrderDTO orderDTO) {

        // todo : validate orderDTO, enough ingredients, price
        //  -- send to confirmation page with receipt and render valid orderDTO OR
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

        // todo : validate prices
        if (!validationResponse.get("availabilityErrors").isEmpty() || !validationResponse.get("priceErrors").isEmpty()) {
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
        //todo :  get orderId,
        // send to ordering/order-confirmation with orderID,


        return "redirect:/order/confirmOrder?orderId=" + orderId;
    }

    @GetMapping("/confirmOrder")
    public String showConfirmation(Model model, @RequestParam("orderId") int orderId) {


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
