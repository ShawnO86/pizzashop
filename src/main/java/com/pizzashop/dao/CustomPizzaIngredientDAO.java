package com.pizzashop.dao;

import com.pizzashop.entities.CustomPizzaIngredient;

import java.util.List;

public interface CustomPizzaIngredientDAO {
    List<CustomPizzaIngredient> findAllById(int id);
}
