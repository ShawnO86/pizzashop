package com.pizzashop.dao;

import com.pizzashop.entities.CustomPizza;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
public class CustomPizzaDAOImpl implements CustomPizzaDAO {
    EntityManager em;

    @Autowired
    public CustomPizzaDAOImpl(EntityManager em) {
        this.em = em;
    }

    @Override
    @Transactional
    public void save(CustomPizza customPizza) {
        em.persist(customPizza);
    }
}
