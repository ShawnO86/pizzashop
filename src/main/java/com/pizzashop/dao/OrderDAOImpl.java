package com.pizzashop.dao;

import com.pizzashop.entities.Order;
import jakarta.persistence.EntityManager;
import org.springframework.stereotype.Repository;

import java.awt.*;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public class OrderDAOImpl implements OrderDAO {
    EntityManager em;

    public OrderDAOImpl(EntityManager em) {
        this.em = em;
    }

    // need??? - countOrdersByMenuItem, countAllOrders...

    @Override
    public List<Order> findAllOrders() {
        return List.of();
    }

    @Override
    public List<Order> findAllOrdersByDate(LocalDateTime date) {
        return List.of();
    }

    @Override
    public List<Order> findAllOrdersByDateRange(LocalDateTime start, LocalDateTime end) {
        return List.of();
    }

    @Override
    public List<Order> findAllOrdersByUsername(String username) {
        return List.of();
    }

    @Override
    public List<Order> findAllOrdersByMenuItem(MenuItem menuItem) {
        return List.of();
    }

}
