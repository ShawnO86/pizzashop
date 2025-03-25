package com.pizzashop.dao;

import com.pizzashop.entities.Ingredient;

import java.util.List;

public interface IngredientDAO {
    Ingredient findById(int id);

    Ingredient findByName(String name);

    List<Ingredient> findAll();

    void save(Ingredient ingredient);

    int deleteById(int id);

    void update(Ingredient ingredient);
}
