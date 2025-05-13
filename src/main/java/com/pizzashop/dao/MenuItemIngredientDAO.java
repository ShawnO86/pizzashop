package com.pizzashop.dao;

import com.pizzashop.entities.MenuItemIngredient;

import java.util.List;

public interface MenuItemIngredientDAO {
    List<MenuItemIngredient> findAll();

    List<MenuItemIngredient> findAllByIngredientId(Integer ingredientId);

    List<MenuItemIngredient> findAllJoinFetchMenuIngredients();

    MenuItemIngredient findById(Integer id);

    void deleteByMenuItemId(Integer menuItemId);
}
