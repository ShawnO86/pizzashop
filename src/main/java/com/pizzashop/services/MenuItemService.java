package com.pizzashop.services;

import com.pizzashop.dto.IngredientDTO;
import com.pizzashop.entities.Ingredient;
import com.pizzashop.entities.MenuItem;

import java.util.List;
import java.util.Map;

public interface MenuItemService {
    Ingredient findIngredientByName(String name);

    Ingredient findIngredientById(int id);

    List<Ingredient> findAllIngredients();

    void saveIngredient(IngredientDTO ingredientDTO);

    void updateIngredient(int ingredientId, IngredientDTO ingredientDTO);

    void deleteIngredient(int id);

    void mapIngredientsToMenuItem(MenuItem menuItem, Map<String, Integer> ingredientsQuantities);
}
