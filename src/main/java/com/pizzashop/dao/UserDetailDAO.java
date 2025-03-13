package com.pizzashop.dao;

import com.pizzashop.entities.UserDetail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserDetailDAO extends JpaRepository<UserDetail, Integer> {
}
