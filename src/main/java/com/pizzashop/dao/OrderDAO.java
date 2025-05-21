package com.pizzashop.dao;

import com.pizzashop.entities.Order;

import java.util.List;

public interface OrderDAO {

    Order save(Order order);

    Order findById(int id);

    Order findByIdJoinFetchUserDetails(int id);

    Order findByUsername(String username);

    List<Order> findAllIncompleteJoinFetchUserDetails();

    void update(Order order);
}
