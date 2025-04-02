package com.pizzashop.controllers;

import com.pizzashop.dao.MenuItemDAO;
import com.pizzashop.entities.MenuCategoryEnum;
import com.pizzashop.entities.MenuItem;
import com.pizzashop.services.MenuItemService;
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
    private final OrderService orderService;
    private final MenuItemService menuItemService;

    @Autowired
    public OrderController(MenuItemDAO menuItemDAO, OrderService orderService, MenuItemService menuItemService) {
        this.menuItemDAO = menuItemDAO;
        this.orderService = orderService;
        this.menuItemService = menuItemService;
    }


    @GetMapping("/showMenu")
    public String showOrderForm(Model model) {
        model.addAttribute("heading", "Hungry? Create an order!");

        Map<String, List<MenuItem>> menuItemsByCategory = seperateMenuItemsByCategory(menuItemDAO.findAllAvailable());
        model.addAttribute("menuItemsByCategory", menuItemsByCategory);

        return "ordering/orderForm";
    }


    // ToDo: add new request params for custom pizzas with 'required = false'
    //  -- should custom pizzas be nested arrays separating ingredients for each pizza?
    @PostMapping("/processOrder")
    public String processOrder(Model model,
                               @RequestParam(value = "menuItemsIdList", required = false) List<Integer> menuItemsIdList,
                               @RequestParam(value = "menuDishNamesList", required = false) String[] menuDishNamesList,
                               @RequestParam(value = "menuItemsAmountsList", required = false) int[] menuItemsAmountsArr,
                               @RequestParam(value = "pizzaIngredientsIdList", required = false) List<Integer[]> pizzaIngredientsIdList) {

        System.out.println("Menu Items ID: " + menuItemsIdList +
                "\n Menu Dish Name: " + Arrays.toString(menuDishNamesList) +
                "\n Menu Item Amounts: " + Arrays.toString(menuItemsAmountsArr) +
                "\n Pizza Ingredients ID: " + pizzaIngredientsIdList);

        // checks for difference in array's lengths, if there are no items added,
        // or item amounts over amount possible based on inventory
        String errMsg = "";

        // todo : check if menuItems OR custom pizza lists are empty
        if (menuItemsIdList == null || menuItemsAmountsArr == null) {
            errMsg = "No menu items added!";
        } else if (menuItemsIdList.size() != menuItemsAmountsArr.length) {
            errMsg = "Menu items and quantity mismatch!";
        }

        if (errMsg.isEmpty()) {
            Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            String username = ((UserDetails) principal).getUsername();

            List<List<String>> orderResult = orderService.submitOrderForFulfillment(menuItemsIdList, menuDishNamesList, menuItemsAmountsArr, username);
            String resultText = orderResult.getFirst().getFirst();

            if (!resultText.equals("Success!")) {
                Map<String, List<MenuItem>> menuItemsByCategory = seperateMenuItemsByCategory(menuItemDAO.findAllAvailable());
                model.addAttribute("menuItemsByCategory", menuItemsByCategory);
                model.addAttribute("heading", "Hungry? Create an order!");

                switch (resultText) {
                    case "Item mismatch!":
                        List<String> notFound = orderResult.get(1);
                        model.addAttribute("orderError", resultText + ". The following dishes were not found:");
                        model.addAttribute("notFound", notFound);
                        break;
                    case "Not enough inventory!":
                        List<String> toLowStock = orderResult.get(1);
                        model.addAttribute("orderError", resultText);
                        model.addAttribute("lowStock", toLowStock);
                        model.addAttribute("menuItemsIdList", menuItemsIdList);
                        model.addAttribute("menuDishNamesList", menuDishNamesList);
                        model.addAttribute("menuItemsAmountsList", menuItemsAmountsArr);
                        break;
                    default:
                        model.addAttribute("orderError", "There was an error.");
                        break;
                }
                return "ordering/orderForm";

            } else {
                model.addAttribute("heading", "Your order has been submitted!");
                // todo : split receipt at '@' symbol for alignment?
                //  -- split into String arrays (2 length), [0] = qty -- name, [1] = $cost ea. = total
                List<String> receipt = orderResult.get(1);
                String totalPrice = orderResult.get(2).getFirst();
                model.addAttribute("receipt", receipt);
                model.addAttribute("total", totalPrice);
                return "ordering/order-confirmation";
            }

        } else {
            Map<String, List<MenuItem>> menuItemsByCategory = seperateMenuItemsByCategory(menuItemDAO.findAllAvailable());
            model.addAttribute("menuItemsByCategory", menuItemsByCategory);
            model.addAttribute("orderError", errMsg);
            model.addAttribute("heading", "Hungry? Create an order!");
            model.addAttribute("menuItemsIdList", menuItemsIdList);
            model.addAttribute("menuDishNamesList", menuDishNamesList);
            model.addAttribute("menuItemsAmountsList", menuItemsAmountsArr);

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
