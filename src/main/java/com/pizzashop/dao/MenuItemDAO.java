package com.pizzashop.dao;

import com.pizzashop.entities.MenuItem;

import java.util.List;

public interface MenuItemDAO {
    MenuItem findByName(String name);

    MenuItem findById(int id);

    List<MenuItem> findAll();

    List<MenuItem> findAllAvailable();

    void save(MenuItem menuItem);

    int deleteById(int id);

    void update(MenuItem menuItem);

}
