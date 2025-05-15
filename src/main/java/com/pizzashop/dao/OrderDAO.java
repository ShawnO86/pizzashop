package com.pizzashop.dao;

import com.pizzashop.entities.Order;

import java.util.List;

public interface OrderDAO {

    Integer save(Order order);

    Order findById(Integer id);

    Order findByUsername(String username);

    List<Order> findAllIncomplete();
}
