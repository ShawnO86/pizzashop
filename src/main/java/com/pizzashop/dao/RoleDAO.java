package com.pizzashop.dao;

import com.pizzashop.entities.Role;
import com.pizzashop.entities.RoleEnum;

import java.util.List;

public interface RoleDAO {
    Role findByRole(RoleEnum role);

    List<Role> findAll();
}
