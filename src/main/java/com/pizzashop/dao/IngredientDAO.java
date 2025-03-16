package com.pizzashop.dao;

import com.pizzashop.entities.Ingredient;

import java.util.List;

public interface IngredientDAO {
    Ingredient findByName(String name);

    List<Ingredient> findAll();

    void save(Ingredient ingredient);

    void delete(Ingredient ingredient);

    void update(Ingredient ingredient);
}
