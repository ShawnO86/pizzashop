package com.pizzashop.dao;

import com.pizzashop.entities.User;

public interface UserDAO {
    User findByUsername(String username);

    User findByUsernameJoinFetchOrders(String username);

    void save(User user);

    void update(User user);
}
