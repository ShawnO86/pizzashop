package com.pizzashop.dao;

import com.pizzashop.entities.User;

import java.util.List;

public interface UserDAO {
    User findByUsername(String username);

    User findByUsernameJoinFetchOrders(String username);

    List<User> findAll();

    void save(User user);

    void update(User user);

    void delete(User user);
}
