package com.pizzashop.services;

import com.pizzashop.dao.IngredientDAO;
import com.pizzashop.dao.MenuItemDAO;
import com.pizzashop.dto.CustomPizzaDTO;
import com.pizzashop.dto.OrderDTO;
import com.pizzashop.dto.OrderMenuItemDTO;

import com.pizzashop.dto.ToppingDTO;
import com.pizzashop.entities.Ingredient;
import com.pizzashop.entities.MenuItem;

import com.pizzashop.entities.PizzaSizeEnum;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class OrderServiceImpl implements OrderService {

    private final MenuItemDAO menuItemDAO;
    private final IngredientDAO ingredientDAO;

    @Autowired
    public OrderServiceImpl(MenuItemDAO menuItemDAO, IngredientDAO ingredientDAO) {
        this.menuItemDAO = menuItemDAO;
        this.ingredientDAO = ingredientDAO;
    }

    // todo: check ingredients are available for all pizzas combined!
    //  -- if no errors, reduce ingredients used in menuItems/customPizzas in order and update menuItems amt available "updateMenuItemAmountAvailable(MenuItem)"
    //  -- ingredients reduced need to have menuItems updated.. "updateAllMenuItemsAmountAvailableByIngredient(Ingredient)"
    //  -- validate prices for menuitems, custom pizzas, and total

    @Override
    public List<String> submitOrderForValidation(OrderDTO order) {
        List<String> availabilityErrors = new ArrayList<>();

        availabilityErrors.addAll(validateMenuItems(order));
        availabilityErrors.addAll(validatePizzaItems(order));

        return availabilityErrors;
    }

    private List<String> validateMenuItems(OrderDTO order) {
        List<String> availabilityErrors = new ArrayList<>();
        List<OrderMenuItemDTO> orderMenuItemDTOs = new ArrayList<>(order.getMenuItemList());
        if (orderMenuItemDTOs.isEmpty()) {
            return availabilityErrors;
        }
        // gets list of menuItemIds to use IN clause for single query and compare with database records
        List<Integer> menuItemIds = new ArrayList<>();
        for (OrderMenuItemDTO orderMenuItemDTO : orderMenuItemDTOs) {
            menuItemIds.add(orderMenuItemDTO.getMenuItemID());
        }
        List<MenuItem> availableMenuItems = menuItemDAO.findAllAvailableIn(menuItemIds);
        Map<Integer, MenuItem> menuItemIdMap = new HashMap<>();
        for (MenuItem menuItem : availableMenuItems) {
            menuItemIdMap.put(menuItem.getId(), menuItem);
        }
        // uses a map for quick lookup time of found available menu items from database
        // adds error string and removes menuItem DTO element from end of list using reverse loop
        // DTO will get reused without removed element if there is an error.
        for (int i = orderMenuItemDTOs.size() - 1; i >= 0; i--) {
            OrderMenuItemDTO currentMenuItemDTO = orderMenuItemDTOs.get(i);
            MenuItem currentMenuItem = menuItemIdMap.get(currentMenuItemDTO.getMenuItemID());
            if (currentMenuItem == null) {
                availabilityErrors.add(currentMenuItemDTO.getMenuItemName() + " is not available at this time.");
                orderMenuItemDTOs.remove(i);
            } else if (currentMenuItem.getAmountAvailable() < currentMenuItemDTO.getMenuItemAmount()) {
                availabilityErrors.add("Cannot add " + currentMenuItemDTO.getMenuItemAmount() + " " + currentMenuItemDTO.getMenuItemName() +
                        ", only " + currentMenuItem.getAmountAvailable() + " currently available. Your cart is updated to reflect available quantity.");
                currentMenuItemDTO.setMenuItemAmount(currentMenuItem.getAmountAvailable());
            }
        }
        order.setMenuItemList(orderMenuItemDTOs);
        return availabilityErrors;
    }

    private List<String> validatePizzaItems(OrderDTO order) {
        List<String> availabilityErrors = new ArrayList<>();
        // If need to modify must create new arraylist
        List<CustomPizzaDTO> customPizzaDTOs = order.getCustomPizzaList();
        if (customPizzaDTOs == null || customPizzaDTOs.isEmpty()) {
            return availabilityErrors;
        }

        availabilityErrors.addAll(validatePizzaIngredients(customPizzaDTOs));
        return availabilityErrors;
    }

    private List<String> validatePizzaIngredients(List<CustomPizzaDTO> customPizzaDTOs) {
        //counts ingredients used, adds to hashmap with id as key
        //used for checking amounts with db.
        Map<Integer, Integer> ingredientsCount = new HashMap<>();

        List<ToppingDTO> toppings;
        List<ToppingDTO> extraToppings;
        PizzaSizeEnum size;

        for (CustomPizzaDTO customPizzaDTO : customPizzaDTOs) {
            toppings = customPizzaDTO.getToppings();
            extraToppings = customPizzaDTO.getExtraToppings();
            size = customPizzaDTO.getPizzaSize().getSize();
            if (toppings != null && !toppings.isEmpty()) {
                for (ToppingDTO topping : toppings) {
                    ingredientsCount.put(topping.getId(), size.getIngredientAmount() * customPizzaDTO.getQuantity());
                }
            }
            if (extraToppings != null && !extraToppings.isEmpty()) {
                for (ToppingDTO topping : extraToppings) {
                    ingredientsCount.put(topping.getId(), size.getExtraIngredientAmount() * customPizzaDTO.getQuantity());
                }
            }
        }

        return checkIngredientsWithDB(ingredientsCount);
    }

    private List<String> checkIngredientsWithDB(Map<Integer, Integer> ingredientsCount) {
        List<String> availabilityErrors = new ArrayList<>();
        List<Integer> ingredientIds = new ArrayList<>(ingredientsCount.keySet());
        List<Ingredient> toppingIngredients = ingredientDAO.findAllIn(ingredientIds);
        //checks for smaller pizza available
        for (Ingredient ingredient : toppingIngredients) {
            if (ingredient.getCurrentStock() < ingredientsCount.get(ingredient.getId())) {
                List<String> pizzasAvailable = new ArrayList<>();
                String errorMessage = "Sorry, not enough " + ingredient.getIngredientName() + " in stock for this order";

                for (int i = 0; i < PizzaSizeEnum.values().length; i++) {
                    PizzaSizeEnum size = PizzaSizeEnum.values()[i];
                    int pizzaAmt = ingredient.getCurrentStock() / size.getIngredientAmount();
                    if (pizzaAmt > 0) {
                        pizzasAvailable.add(pizzaAmt + " " + size.name().toLowerCase());
                    }
                }

                if (!pizzasAvailable.isEmpty()) {
                    if (pizzasAvailable.size() > 1) {
                        pizzasAvailable.set(pizzasAvailable.size() - 1, "or " + pizzasAvailable.getLast());
                    }
                    errorMessage += ", we can make: " + String.join(", ", pizzasAvailable) + " pizza(s) with " + ingredient.getIngredientName() + ".";
                } else {
                    errorMessage += ", please choose another topping.";
                }

                availabilityErrors.add(errorMessage);
            }
        }

        return availabilityErrors;
    }


}