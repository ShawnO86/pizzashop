package com.pizzashop.services;

import com.pizzashop.dao.IngredientDAO;
import com.pizzashop.dao.MenuItemDAO;
import com.pizzashop.dao.OrderDAO;

import com.pizzashop.dto.CustomPizzaDTO;
import com.pizzashop.dto.OrderDTO;
import com.pizzashop.dto.OrderMenuItemDTO;
import com.pizzashop.dto.ToppingDTO;

import com.pizzashop.entities.Ingredient;
import com.pizzashop.entities.MenuItem;
import com.pizzashop.entities.Order;
import com.pizzashop.entities.PizzaSizeEnum;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.*;

@Service
public class OrderServiceImpl implements OrderService {

    private final MenuItemDAO menuItemDAO;
    private final IngredientDAO ingredientDAO;
    private final OrderDAO orderDAO;

    private final List<String> availabilityErrors = new ArrayList<>();
    private final List<String> priceErrors = new ArrayList<>();

    @Autowired
    public OrderServiceImpl(MenuItemDAO menuItemDAO, IngredientDAO ingredientDAO, OrderDAO orderDAO) {
        this.menuItemDAO = menuItemDAO;
        this.ingredientDAO = ingredientDAO;
        this.orderDAO = orderDAO;
    }

    // todo: if no errors, reduce ingredients used in menuItems/customPizzas in order and update menuItems amt available "updateMenuItemAmountAvailable(MenuItem)"
    //  -- ingredients reduced need to have menuItems updated.. "updateAllMenuItemsAmountAvailableByIngredient(Ingredient)"
    //  -- validate prices for menuitems, custom pizzas, and total

    @Override
    public int submitOrder(OrderDTO order, String username) {
        // todo : check for correct Id return value after save
        Order orderEntity = new Order();


        return orderDAO.save(orderEntity);
    }

    @Override
    public Map<String, List<String>> submitOrderForValidation(OrderDTO order) {
        Map<String, List<String>> errors = new HashMap<>();

        if (order.getMenuItemList() != null && !order.getMenuItemList().isEmpty()) {
            validateMenuItems(order);
        }
        if (order.getCustomPizzaList() != null && !order.getCustomPizzaList().isEmpty()) {
            validatePizzaIngredients(order.getCustomPizzaList());
        }

        validateOrderPrice(order);

        errors.put("availabilityErrors", availabilityErrors);
        errors.put("priceErrors", priceErrors);

        return errors;
    }

    private void validateMenuItems(OrderDTO order) {
        // copy orderMenuItemDTOs to ArrayList Impl for making modifications to it
        List<OrderMenuItemDTO> orderMenuItemDTOs = new ArrayList<>(order.getMenuItemList());
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
        // adds error string and removes menuItem DTO element from end of sorted list using reverse loop
        // if no availability errors, check for correct price
        int menuItemsTotalPrice = 0;
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
            } else if (currentMenuItem.getPriceCents() != currentMenuItemDTO.getPricePerItem()) {
                priceErrors.add(currentMenuItem.getDishName() + " does not match our price. Your cart is updated to reflect actual price.");
                currentMenuItemDTO.setPricePerItem(currentMenuItem.getPriceCents());
            }
        }
        order.setMenuItemList(orderMenuItemDTOs);
    }

    private void validatePizzaIngredients(List<CustomPizzaDTO> customPizzaDTOs) {
        //counts ingredients used, adds to hashmap with id as key, count as value
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

        checkIngredientsWithDB(ingredientsCount);
    }

    private void checkIngredientsWithDB(Map<Integer, Integer> ingredientsCount) {
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
    }

    private void validateOrderPrice(OrderDTO order) {
        int pizzasPrice = 0;
        int menuPrice = 0;


        if (order.getMenuItemList() != null && !order.getMenuItemList().isEmpty()) {
            menuPrice = getMenuItemsPrice(order.getMenuItemList());
        }
        if (order.getCustomPizzaList() != null && !order.getCustomPizzaList().isEmpty()) {
            pizzasPrice = validatePizzaDTOPrices(order.getCustomPizzaList());
        }

        int orderPrice = menuPrice + pizzasPrice;
        if (order.getTotalPrice() != orderPrice) {
            order.setTotalPrice(orderPrice);
            priceErrors.add("Cart price does not match our calculated price. Your cart is updated to reflect actual price.");
        }
    }

    private int getMenuItemsPrice(List<OrderMenuItemDTO> orderMenuItemDTOs) {
        int price = 0;

        for (OrderMenuItemDTO orderMenuItemDTO : orderMenuItemDTOs) {
            price += orderMenuItemDTO.getMenuItemAmount() * orderMenuItemDTO.getPricePerItem();
        }

        return price;
    }

    private int validatePizzaDTOPrices(List<CustomPizzaDTO> customPizzaDTOs) {
        int totalPrice = 0;
        for (CustomPizzaDTO customPizzaDTO : customPizzaDTOs) {
            int pizzaPrice = 0;
            List<ToppingDTO> toppings = customPizzaDTO.getToppings();
            List<Integer> toppingIds = new ArrayList<>();
            if (toppings != null && !toppings.isEmpty()) {
                for (ToppingDTO topping : toppings) {
                    toppingIds.add(topping.getId());
                }
            }

            List<ToppingDTO> extraToppings = customPizzaDTO.getExtraToppings();
            List<Integer> extraToppingIds = new ArrayList<>();
            if (extraToppings != null && !extraToppings.isEmpty()) {
                for (ToppingDTO topping : extraToppings) {
                    extraToppingIds.add(topping.getId());
                }
            }

            List<Ingredient> toppingIngredients = ingredientDAO.findAllIn(toppingIds);
            List<Ingredient> extraIngredients = ingredientDAO.findAllIn(extraToppingIds);
            PizzaSizeEnum size = customPizzaDTO.getPizzaSize().getSize();
            String sizeTitleCase = StringUtils.capitalize(size.name().toLowerCase());
            int toppingAmount = size.getIngredientAmount();
            int extraToppingAmount = size.getExtraIngredientAmount();

            MenuItem basePizza = menuItemDAO.findByName(sizeTitleCase + " Cheese Pizza");
            if (basePizza == null) {
                availabilityErrors.add("Sorry, " + size.name().toLowerCase() + " pizza is not available at this time.");
                return 0;
            } else if (basePizza.getAmountAvailable() < customPizzaDTO.getQuantity()) {
                availabilityErrors.add("Sorry, not enough stock for " + customPizzaDTO.getQuantity() + " pizzas, cart is updated to reflect amount available.");
                customPizzaDTO.setQuantity(basePizza.getAmountAvailable());
                return 0;
            }

            System.out.println("*** base pizza price: " + basePizza.getPriceCents());
            pizzaPrice = basePizza.getPriceCents();

            for (Ingredient ingredient : toppingIngredients) {
                System.out.println("*** topping " + ingredient.getIngredientName() +  " price: " + ingredient.getCentsPricePer() + " amount: " + toppingAmount + " total: " + (ingredient.getCentsPricePer() * toppingAmount));
                pizzaPrice += ingredient.getCentsPricePer() * toppingAmount;
            }

            for (Ingredient ingredient : extraIngredients) {
                pizzaPrice += ingredient.getCentsPricePer() * extraToppingAmount;
            }

            System.out.println("DTO price: " + customPizzaDTO.getPricePerPizza() + "actual price: " + pizzaPrice);

            if (pizzaPrice != customPizzaDTO.getPricePerPizza()) {
                customPizzaDTO.setPricePerPizza(pizzaPrice);
                priceErrors.add(customPizzaDTO.getPizzaName() + "'s price does not match our price for this order. It is updated to reflect actual price.");
            }
            if (pizzaPrice * customPizzaDTO.getQuantity() != customPizzaDTO.getTotalPizzaPrice()) {
                customPizzaDTO.setTotalPizzaPrice(pizzaPrice * customPizzaDTO.getQuantity());
                priceErrors.add(customPizzaDTO.getPizzaName() + "'s total price does not match our calculated price for this order. It is updated to reflect actual price.");
            }

            totalPrice += customPizzaDTO.getTotalPizzaPrice();
        }
        return totalPrice;
    }


}