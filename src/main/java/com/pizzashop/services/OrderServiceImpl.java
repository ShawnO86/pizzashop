package com.pizzashop.services;

import com.pizzashop.dao.*;
import com.pizzashop.entities.*;

import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class OrderServiceImpl implements OrderService {

    private final IngredientDAO ingredientDAO;
    private final MenuItemDAO menuItemDAO;
    private final OrderDAO orderDAO;
    private final UserDAO userDAO;
    private final MenuItemService menuItemService;

    @Autowired
    public OrderServiceImpl(IngredientDAO ingredientDAO, MenuItemDAO menuItemDAO, OrderDAO orderDAO, UserDAO userDAO, MenuItemService menuItemService) {
        this.ingredientDAO = ingredientDAO;
        this.menuItemDAO = menuItemDAO;
        this.orderDAO = orderDAO;
        this.userDAO = userDAO;
        this.menuItemService = menuItemService;
    }

    // todo : figure out how to set custom pizza items and quantities in order
    //  -- :
    @Override
    @Transactional
    public List<List<String>> submitOrderForFulfillment(List<Integer> menuItemsIds, String[] menuItemsNames, int[] menuItemQuantities, String username) {
        List<List<String>> orderResult = new ArrayList<>();

        // do same for custom pizza items but for each pizza's ingredients
        List<MenuItem> menuItems = new ArrayList<>();

        // if order has menu items
        if (!menuItemsIds.isEmpty()) {
            menuItems = menuItemDAO.findAllIn(menuItemsIds);

            // verify all menu item ids are found/available
            // returns menuItemsNames not in menuItems list from DAO
            if (menuItems.size() != menuItemsIds.size()) {
                // orderResult(0)(0)
                orderResult.add(List.of("Item mismatch!"));
                // orderResult(1)(...)
                orderResult.add(getMenuItemNamesNotAvailable(menuItems, menuItemsIds, menuItemsNames));
                return orderResult;
            }

            // verify amounts available against quantities requested
            // returns list of menuItem.dishName + menuItem.amountAvailable if not enough inventory
            List<String> qtyNotInInventory = new ArrayList<>();
            boolean anyOver = false;
            for (int i = 0; i < menuItems.size(); i++) {
                if (menuItems.get(i).getAmountAvailable() < menuItemQuantities[i]) {
                    qtyNotInInventory.add(menuItems.get(i).getDishName() + " -- " + menuItems.get(i).getAmountAvailable());
                    anyOver = true;
                }
            }
            if (anyOver) {
                // orderResult(0)(0)
                orderResult.add(List.of("Not enough inventory!"));
                // orderResult(1)(...)
                orderResult.add(qtyNotInInventory);
                return orderResult;
            }
        }

        // List<CustomPizza> customPizzas = new ArrayList<>();
/*        if (customPizzaIngredientIds.size() >= 1) {
            customPizzas = ingredientDAO ...
        }*/

        // create order object with user attached
        User user = userDAO.findByUsername(username);
        Order newOrder = new Order(user, LocalDateTime.now());

        List<String> orderReceiptItems = new ArrayList<>();

        // todo : same for custom pizzas - add to orderReceiptItems(1)
        //  -- use orderMenuItems to build receipt, calculate total price, and reduce inventory

        for (int i = 0; i < menuItems.size(); i++) {
            OrderMenuItem orderMenuItem = new OrderMenuItem(newOrder);
            MenuItem menuItem = menuItems.get(i);

            orderMenuItem.setMenuItem(menuItem);
            orderMenuItem.setItemQuantity(menuItemQuantities[i]);

            newOrder.addMenuItem(orderMenuItem);

            // will have to do this in the customPizzasOrders loop as well
            orderReceiptItems.add(
                    menuItemQuantities[i] + " -- " + menuItem.getDishName() + " @ " + menuItem.getPriceCents() + " ea. = " + (menuItem.getPriceCents() * menuItemQuantities[i]));
        }

        this.updateInventoryIngredientQuantities(newOrder.getOrderMenuItems(), menuItemQuantities);

        newOrder.setFinal_price_cents(this.calculateTotalOrderPrice(newOrder.getOrderMenuItems()));

        newOrder.setIs_complete(false);

        // orderResult(0)(0) result message
        orderResult.add(List.of("Success!"));
        // orderResult(1)(...) receipt
        orderResult.add(orderReceiptItems);
        // orderResult(2)(0) total price
        orderResult.add(List.of(String.valueOf(newOrder.getFinal_price_cents())));

        orderDAO.save(newOrder);

        return orderResult;
    }

    // reduce inventory used from menuItems in order and update available menuItems.
    @Override
    @Transactional
    public void updateInventoryIngredientQuantities(List<OrderMenuItem> orderItems, int[] quantities) {
        // todo : get either menuItemIngredient or customPizzaIngredient list for each item,
        //  -- orderItems.get(0).get(i).menuItem or orderItems.get(1).get(i).customPizza
        //  --

        for (int i = 0; i < orderItems.size(); i++) {
            if (orderItems.get(i).getMenuItem() != null) {
                MenuItem menuItem = orderItems.get(i).getMenuItem();
                List<MenuItemIngredient> ingredients = menuItem.getMenuItemIngredients();
                for (MenuItemIngredient menuItemIngredient : ingredients) {
                    Ingredient currentIngredient = menuItemIngredient.getIngredient();
                    int currentStock = currentIngredient.getCurrentStock();
                    int used = menuItemIngredient.getQuantityUsed() * quantities[i];
                    currentIngredient.setCurrentStock(currentStock - used);
                    ingredientDAO.update(currentIngredient);
                }

                menuItem.setAmountAvailable(menuItemService.updateMenuItemAmountAvailable(menuItem));

                if (menuItem.getAmountAvailable() < 1) {
                    menuItem.setIsAvailable(false);
                }
            }

            if (orderItems.get(i).getCustomPizza() != null) {
                // todo : getCustomPizza(),
                //  -- get list of CustomPizzaIngredient,
                //  -- update each ingredient used based on quantityUsed
            }

        }
    }

    private List<String> getMenuItemNamesNotAvailable(List<MenuItem> menuItems, List<Integer> menuItemsIds, String[] menuItemsNames) {
        List<String> namesNotInMenu = new ArrayList<>();
        for (int i = 0; i < menuItemsIds.size(); i++) {
            boolean found = false;
            for (MenuItem menuItem : menuItems) {
                if (menuItem.getId().equals(menuItemsIds.get(i))) {
                    found = true;
                    break;
                }
            }
            if (!found) {
                namesNotInMenu.add(menuItemsNames[i]);
            }
        }
        return namesNotInMenu;
    }

    private int calculateTotalOrderPrice(List<OrderMenuItem> orderItems) {
        int totalPrice = 0;

        for (OrderMenuItem orderItem : orderItems) {
            int quantity = orderItem.getItemQuantity();

            if (orderItem.getMenuItem() != null) {
                totalPrice += orderItem.getMenuItem().getPriceCents() * quantity;
            } else if (orderItem.getCustomPizza() != null) {
                totalPrice += orderItem.getCustomPizza().getPriceCents() * quantity;
            }
        }

        return totalPrice;
    }

}
