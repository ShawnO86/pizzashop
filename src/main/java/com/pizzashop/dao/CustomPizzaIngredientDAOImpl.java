package com.pizzashop.dao;

import com.pizzashop.entities.CustomPizzaIngredient;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class CustomPizzaIngredientDAOImpl implements CustomPizzaIngredientDAO {
    final EntityManager em;

    @Autowired
    public CustomPizzaIngredientDAOImpl(EntityManager em) {
        this.em = em;
    }

    @Override
    public List<CustomPizzaIngredient> findAllById(int id) {
        TypedQuery<CustomPizzaIngredient> query = em.createQuery("FROM CustomPizzaIngredient c WHERE c.ingredient.id = :id", CustomPizzaIngredient.class);
        query.setParameter("id", id);
        return query.getResultList();
    }
}
