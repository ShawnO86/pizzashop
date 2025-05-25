package com.pizzashop.dao;

import com.pizzashop.entities.Order;

import java.time.LocalDate;
import java.util.List;

public interface OrderDAO {

    Order save(Order order);

    Order findById(int id);

    Order findByIdJoinFetchUserDetails(int id);

    Order findByUsername(String username);

    List<Order> findAllIncompleteJoinFetchUserDetails();

    List<Order> findAllByDateRange(LocalDate from, LocalDate to);

    List<Order> findAllFulfilledByIdInDateRange(LocalDate from, LocalDate to, String username);

    void update(Order order);
}
