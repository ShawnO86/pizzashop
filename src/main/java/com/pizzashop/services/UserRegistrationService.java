package com.pizzashop.services;

import com.pizzashop.dto.UserRegisterDTO;
import com.pizzashop.entities.Role;
import com.pizzashop.entities.User;
import org.springframework.security.core.userdetails.UserDetailsService;

import java.util.Optional;
import java.util.Set;

public interface UserRegistrationService extends UserDetailsService {
    Optional<User> findByUserName(String userName);

    void save(UserRegisterDTO userRegisterDTO, Set<Role> roles);
}
