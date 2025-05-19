package com.pizzashop.dao;

import com.pizzashop.entities.CustomPizza;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.TypedQuery;
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
    public CustomPizza findById(int id) {
        TypedQuery<CustomPizza> query = em.createQuery("FROM CustomPizza c WHERE c.id = :id", CustomPizza.class);
        query.setParameter("id", id);
        try {
            return query.getSingleResult();
        } catch (NoResultException e) {
            return null;
        }
    }

    @Override
    @Transactional
    public void save(CustomPizza customPizza) {
        em.persist(customPizza);
    }
}
