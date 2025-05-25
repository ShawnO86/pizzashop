package com.pizzashop.dao;

import com.pizzashop.entities.CustomPizza;

import java.util.List;

public interface CustomPizzaDAO {
    CustomPizza findById(int id);

    CustomPizza findByIdJoinFetchIngredients(int id);

    List<CustomPizza> findAllInJoinFetchIngredients(List<Integer> ids);

    void save(CustomPizza customPizza);
}
