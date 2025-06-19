package com.pizzashop.dao;

import com.pizzashop.entities.CustomPizza;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.TypedQuery;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class CustomPizzaDAOImpl implements CustomPizzaDAO {
    final EntityManager em;

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
    public CustomPizza findByIdJoinFetchIngredients(int id) {
        TypedQuery<CustomPizza> query = em.createQuery("FROM CustomPizza c " +
                "JOIN FETCH c.customPizzaIngredients cpi JOIN FETCH cpi.ingredient " +
                "WHERE c.id = :id", CustomPizza.class);
        query.setParameter("id", id);
        try {
            return query.getSingleResult();
        } catch (NoResultException e) {
            return null;
        }
    }

    @Override
    public List<CustomPizza> findAllInJoinFetchIngredients(List<Integer> ids) {
        TypedQuery<CustomPizza> query = em.createQuery("FROM CustomPizza c " +
                "JOIN FETCH c.customPizzaIngredients cpi JOIN FETCH cpi.ingredient " +
                "WHERE c.id IN (:ids)", CustomPizza.class);
        query.setParameter("ids", ids);

        return query.getResultList();
    }

    @Override
    @Transactional
    public void save(CustomPizza customPizza) {
        em.persist(customPizza);
    }
}
