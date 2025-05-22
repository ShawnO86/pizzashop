package com.pizzashop.dao;

import com.pizzashop.entities.User;

import java.util.List;

public interface UserDAO {
    User findById(int userId);

    User findByIdJoinFetchUserDetailsRoles(int userId);

    User findByUsername(String username);

    User findByUsernameJoinFetchRole(String username);

    List<User> findAll();

    List<User> findAllFetchUserDetailsRoles();

    List<User> findAllByLastName(String lastName);

    void save(User user);

    void updateUser(User user);

    void deactivateUser(int userId);

    void activateUser(int userId);
}
