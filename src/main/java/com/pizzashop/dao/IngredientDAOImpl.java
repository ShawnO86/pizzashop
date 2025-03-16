package com.pizzashop.dao;

import com.pizzashop.entities.Ingredient;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.TypedQuery;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class IngredientDAOImpl implements IngredientDAO {
    EntityManager em;

    @Autowired
    public IngredientDAOImpl(EntityManager em) {
        this.em = em;
    }

    @Override
    public Ingredient findByName(String name) {
        TypedQuery<Ingredient> query = em.createQuery("FROM Ingredient WHERE ingredientName = :ingredientName", Ingredient.class);
        query.setParameter("ingredientName", name);
        try {
            return query.getSingleResult();
        } catch (NoResultException e) {
            System.out.println("No ingredient found by name: " + name);
            return null;
        }
    }

    @Override
    public List<Ingredient> findAll() {
        TypedQuery<Ingredient> query = em.createQuery("FROM Ingredient", Ingredient.class);
        return query.getResultList();
    }

    @Override
    @Transactional
    public void save(Ingredient ingredient) {
        em.persist(ingredient);
    }

    @Override
    @Transactional
    public void delete(Ingredient ingredient) {
        em.remove(ingredient);
    }

    @Override
    @Transactional
    public void update(Ingredient ingredient) {
        em.merge(ingredient);
    }
}
