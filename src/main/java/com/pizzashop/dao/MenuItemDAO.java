package com.pizzashop.dao;

import com.pizzashop.entities.MenuItem;

import java.util.List;

public interface MenuItemDAO {
    MenuItem findByName(String name);

    MenuItem findById(int id);

    MenuItem findByNameJoinFetchIngredients(String name);

    MenuItem findByIdJoinFetchIngredients(int id);

    List<MenuItem> findAll();

    List<MenuItem> findAllAvailable();

    List<MenuItem> findAllAvailableIn(List<Integer> ids);

    List<MenuItem> findAllInJoinFetchIngredients(List<Integer> ids);

    void save(MenuItem menuItem);

    int deleteById(int id);

    void update(MenuItem menuItem);

}
