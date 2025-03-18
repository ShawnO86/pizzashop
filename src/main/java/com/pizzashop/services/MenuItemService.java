package com.pizzashop.services;

import com.pizzashop.entities.MenuItem;

import java.util.Map;

public interface MenuItemService {
    void mapIngredientsToMenuItem(MenuItem menuItem, Map<String, Integer> ingredientsQuantities);
}
