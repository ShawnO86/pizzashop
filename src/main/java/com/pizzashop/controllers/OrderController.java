package com.pizzashop.controllers;

import com.pizzashop.dao.IngredientDAO;
import com.pizzashop.dao.MenuItemDAO;
import com.pizzashop.dto.OrderDTO;
import com.pizzashop.entities.Ingredient;
import com.pizzashop.entities.MenuCategoryEnum;
import com.pizzashop.entities.MenuItem;
import com.pizzashop.entities.PizzaSizeEnum;
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
        model.addAttribute("order", new OrderDTO());

        return "ordering/orderForm";
    }

    @PostMapping("/processOrder")
    public String processOrder(Model model, @ModelAttribute("order") OrderDTO orderDTO) {

        // todo : validate orderDTO,
        //  -- send to confirmation page with receipt and render valid orderDTO OR
        //  -- output errors in order form and send orderDTO back

        System.out.println("orderDTO --> \n" + orderDTO);

        String errorMsg = "";
        if (orderDTO.getCustomPizzaList() == null && orderDTO.getMenuItemList() == null) {
            errorMsg = "Nothing in cart!";
        } else if (orderDTO.getTotalPrice() == 630) {
            errorMsg = "Total price is 630!";
        }

        if (!errorMsg.isEmpty()) {
            Map<String, List<MenuItem>> menuItemsByCategory = separateMenuItemsByCategory(menuItemDAO.findAllAvailable());
            List<Ingredient> pizzaToppings = ingredientDAO.findAllPizzaToppings();

            model.addAttribute("cartError", errorMsg);
            model.addAttribute("heading", "Hungry? Create an order!");
            model.addAttribute("menuItemsByCategory", menuItemsByCategory);
            model.addAttribute("pizzaSizes", PizzaSizeEnum.values());
            model.addAttribute("pizzaToppings", pizzaToppings);
            model.addAttribute("order", orderDTO);
            return "ordering/orderForm";
        }

            //for testing
        Map<String, List<MenuItem>> menuItemsByCategory = separateMenuItemsByCategory(menuItemDAO.findAllAvailable());
        List<Ingredient> pizzaToppings = ingredientDAO.findAllPizzaToppings();
        //model.addAttribute("cartError", errorMsg);
        model.addAttribute("heading", "Hungry? Create an order!");
        model.addAttribute("menuItemsByCategory", menuItemsByCategory);
        model.addAttribute("pizzaSizes", PizzaSizeEnum.values());
        model.addAttribute("pizzaToppings", pizzaToppings);
        //model.addAttribute("order", orderDTO);
        return "ordering/orderForm";



        //return "ordering/order-confirmation";

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
