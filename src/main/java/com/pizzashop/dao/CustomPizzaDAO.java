package com.pizzashop.dao;

import com.pizzashop.entities.CustomPizza;

public interface CustomPizzaDAO {
    CustomPizza findById(int id);

    void save(CustomPizza customPizza);
}
