package com.pizzashop.services;

import com.pizzashop.dto.UserRegisterDTO;
import com.pizzashop.entities.User;
import org.springframework.security.core.userdetails.UserDetailsService;

import java.util.Optional;

public interface UserService extends UserDetailsService {
    public Optional<User> findByUserName(String userName);

    void save(UserRegisterDTO userRegisterDTO);
}
