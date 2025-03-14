package com.pizzashop.dao;

import com.pizzashop.entities.Role;
import com.pizzashop.entities.RoleEnum;

public interface RoleDAO {
    Role findByRole(RoleEnum role);
}
