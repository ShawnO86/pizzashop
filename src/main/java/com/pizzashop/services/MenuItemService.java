package com.pizzashop.services;

import java.util.Map;

public interface MenuItemService {
    void mapIngredientsToMenuItem(String menuItemName, Map<String, Integer> ingredientsQuantities);
}
