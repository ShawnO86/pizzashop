package com.pizzashop.services;

import com.pizzashop.entities.MenuItem;

import java.util.List;

public interface OrderService {

    List<List<String>> submitOrderForFulfillment(List<Integer> menuItems, String[] menuNames, int[] quantities, String username);

    void updateInventoryIngredientQuantities(List<MenuItem> menuItems, int[] quantities);
}
