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

    // todo : figure out how to set custom pizza items
    //  -- :
    @Override
    @Transactional
    public List<List<String>> submitOrderForFulfillment(List<Integer> menuItemsIds, String[] menuItemsNames, int[] quantities, String username) {
        List<MenuItem> menuItems = new ArrayList<>();
        // List<CustomPizza> customPizzas = new ArrayList<>();
        // do same for custom pizza items
        if (!menuItemsIds.isEmpty()) {
            menuItems = menuItemDAO.findAllIn(menuItemsIds);
        }
/*        if (customPizzaIngredientIds.size() >= 1) {
            customPizzas = ingredientDAO ...
        }*/

        List<List<String>> orderResult = new ArrayList<>();

        // verify all menu item ids are found/available
        // returns menuItemsNames not in menuItems list from DAO
        if (menuItems.size() != menuItemsIds.size()) {
            orderResult.add(List.of("Item mismatch!"));
            orderResult.add(getMenuItemNamesNotAvailable(menuItems, menuItemsIds, menuItemsNames));
            return orderResult;
        }


        // verify amounts available against quantities requested
        // returns list of menuItem.dishName + menuItem.amountAvailable if not enough inventory
        List<String> qtyNotInInventory = new ArrayList<>();
        boolean anyOver = false;
        for (int i = 0; i < menuItems.size(); i++) {
            if (menuItems.get(i).getAmountAvailable() < quantities[i]) {
                qtyNotInInventory.add(menuItems.get(i).getDishName() + " -- " + menuItems.get(i).getAmountAvailable());
                anyOver = true;
            }
        }
        if (anyOver) {
            orderResult.add(List.of("Not enough inventory!"));
            orderResult.add(qtyNotInInventory);
            return orderResult;
        }


        User user = userDAO.findByUsername(username);
        Order newOrder = new Order(user, LocalDateTime.now());

        List<OrderMenuItem> orderMenuItems = new ArrayList<>();
        List<String> orderReceiptItems = new ArrayList<>();

        for (int i = 0; i < menuItems.size(); i++) {
            OrderMenuItem orderMenuItem = new OrderMenuItem(newOrder);
            MenuItem menuItem = menuItems.get(i);
            orderMenuItem.setMenuItem(menuItem);
            orderMenuItem.setItemQuantity(quantities[i]);
            orderMenuItems.add(orderMenuItem);

            orderReceiptItems.add(
                    quantities[i] + " -- " + menuItem.getDishName() + " @ " + menuItem.getPriceCents() + " ea. = " + (menuItem.getPriceCents() * quantities[i]));
        }

        this.updateInventoryIngredientQuantities(menuItems, quantities);
        newOrder.setFinal_price_cents(this.calculateTotalOrderPrice(menuItems, quantities));
        newOrder.setIs_complete(false);
        newOrder.setMenuItems(orderMenuItems);

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
    public void updateInventoryIngredientQuantities(List<MenuItem> menuItems, int[] quantities) {
        for (int i = 0; i < menuItems.size(); i++) {
            List<MenuItemIngredient> ingredients = menuItems.get(i).getMenuItemIngredients();
            for (MenuItemIngredient menuItemIngredient : ingredients) {
                Ingredient currentIngredient = menuItemIngredient.getIngredient();
                int currentStock = currentIngredient.getCurrentStock();
                int used = menuItemIngredient.getQuantityUsed() * quantities[i];
                currentIngredient.setCurrentStock(currentStock - used);
                ingredientDAO.update(currentIngredient);
            }
            menuItems.get(i).setAmountAvailable(menuItemService.updateMenuItemAmountAvailable(menuItems.get(i)));
            if (menuItems.get(i).getAmountAvailable() < 1) {
                menuItems.get(i).setIsAvailable(false);
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

    private int calculateTotalOrderPrice(List<MenuItem> menuItems, int[] quantities) {
        int totalPrice = 0;

        for (int i = 0; i < menuItems.size(); i++) {
            //MenuItem currentMenuItem = menuItemDAO.findByName(menuItem.getDishName());
            totalPrice += menuItems.get(i).getPriceCents() * quantities[i];
        }

        return totalPrice;
    }

}
