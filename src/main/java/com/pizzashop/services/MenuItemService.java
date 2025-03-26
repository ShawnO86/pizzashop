package com.pizzashop.services;

import com.pizzashop.dto.IngredientDTO;
import com.pizzashop.dto.MenuItemDTO;
import com.pizzashop.entities.Ingredient;
import com.pizzashop.entities.MenuItem;
import com.pizzashop.entities.MenuItemIngredient;

import java.util.List;

public interface MenuItemService {
    Ingredient findIngredientByName(String name);

    Ingredient findIngredientById(int id);

    List<Ingredient> findAllIngredients();

    void saveIngredient(IngredientDTO ingredientDTO);

    void updateIngredient(int ingredientId, IngredientDTO ingredientDTO);

    List<MenuItemIngredient> deleteIngredient(int id);

    List<MenuItem> findAllMenuItems();

    MenuItem findMenuItemByName(String name);

    MenuItem findMenuItemById(int id);

    void saveMenuItem(MenuItemDTO menuItemDTO);

    void updateMenuItem(int menuItemId, MenuItemDTO menuItemDTO);

    void deleteMenuItem(int menuItemId);

    List<String[]> buildRecipeByMenuItem(MenuItem menuItem);

    List<int[]> buildIngredientIdAmounts(Integer[] ingredientIdsAmountsKeys, Integer[] ingredientIdAmountValues);

    int updateMenuItemAmountAvailable(MenuItem menuItem);
}
