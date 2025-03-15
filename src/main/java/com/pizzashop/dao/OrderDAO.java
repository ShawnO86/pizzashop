package com.pizzashop.dao;

import com.pizzashop.entities.Order;

import java.awt.*;
import java.time.LocalDateTime;
import java.util.List;

public interface OrderDAO {
    List<Order> findAllOrders();

    List<Order> findAllOrdersByDate(LocalDateTime date);

    List<Order> findAllOrdersByDateRange(LocalDateTime start, LocalDateTime end);

    List<Order> findAllOrdersByUsername(String username);

    List<Order> findAllOrdersByMenuItem(MenuItem menuItem);
}
