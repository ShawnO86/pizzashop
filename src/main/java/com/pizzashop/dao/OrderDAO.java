package com.pizzashop.dao;

import com.pizzashop.entities.Order;

public interface OrderDAO {

    Integer save(Order order);

    Order findById(Integer id);

    Order findByUsername(String username);
}
