package com.pizzashop.dao;

import com.pizzashop.entities.Role;
import com.pizzashop.entities.RoleEnum;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RoleDAO extends JpaRepository<Role, Integer> {
    Role findByRole(RoleEnum role);
}
