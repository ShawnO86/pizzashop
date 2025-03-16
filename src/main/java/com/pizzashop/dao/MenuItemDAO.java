package com.pizzashop.dao;

import com.pizzashop.entities.MenuItem;

import java.util.List;

public interface MenuItemDAO {
    MenuItem findByName(String name);

    List<MenuItem> findAll();

    void save(MenuItem menuItem);

    void delete(MenuItem menuItem);

    void update(MenuItem menuItem);

}
