package com.pizzashop.dao;

import com.pizzashop.entities.CustomPizza;

public interface CustomPizzaDAO {
    CustomPizza findById(int id);

    CustomPizza findByIdJoinFetchIngredients(int id);

    void save(CustomPizza customPizza);
}
