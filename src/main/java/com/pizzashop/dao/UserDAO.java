package com.pizzashop.dao;

import com.pizzashop.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserDAO extends JpaRepository<User, Integer> {
    public User findByUsername(String username);
}
