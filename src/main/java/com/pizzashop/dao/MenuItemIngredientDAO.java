package com.pizzashop.dao;

import com.pizzashop.entities.MenuItemIngredient;

import java.util.List;

public interface MenuItemIngredientDAO {
    List<MenuItemIngredient> findAll();

    List<MenuItemIngredient> findAllByMenuItemId(Integer menuItemId);

    List<MenuItemIngredient> findAllByIngredientId(Integer ingredientId);

    MenuItemIngredient findById(Integer id);

    void deleteByMenuItemId(Integer menuItemId);

    void deleteByMenuItemIdIngredientId(Integer menuItemId, Integer ingredientId);
}
