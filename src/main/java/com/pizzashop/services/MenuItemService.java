package com.pizzashop.services;

import com.pizzashop.entities.Ingredient;

import java.util.List;
import java.util.Map;

public interface MenuItemService {
    void mapIngredientsToMenuItem(String menuItemName, Map<String, Integer> ingredientsQuantities);
}
