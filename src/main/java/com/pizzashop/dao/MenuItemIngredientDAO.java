package com.pizzashop.dao;

import com.pizzashop.entities.MenuItemIngredient;

import java.util.List;

public interface MenuItemIngredientDAO {
    List<MenuItemIngredient> findAllByIngredientId(Integer ingredientId);

    List<MenuItemIngredient> findAllJoinFetchMenuIngredients();

    void deleteByMenuItemId(Integer menuItemId);
}
