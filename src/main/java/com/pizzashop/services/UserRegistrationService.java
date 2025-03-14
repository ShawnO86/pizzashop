package com.pizzashop.services;

import com.pizzashop.dto.UserRegisterDTO;
import com.pizzashop.entities.User;
import org.springframework.security.core.userdetails.UserDetailsService;

import java.util.Optional;

public interface UserRegistrationService extends UserDetailsService {
    public Optional<User> findByUserName(String userName);

    public void save(UserRegisterDTO userRegisterDTO);
}
