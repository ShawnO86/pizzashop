package com.pizzashop.dao;

import com.pizzashop.entities.Ingredient;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.Query;
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
    public Ingredient findById(int id) {
        TypedQuery<Ingredient> query = em.createQuery("FROM Ingredient WHERE id = :id", Ingredient.class);
        query.setParameter("id", id);
        try {
            return query.getSingleResult();
        } catch (NoResultException e) {
            return null;
        }
    }

    @Override
    public Ingredient findByName(String name) {
        TypedQuery<Ingredient> query = em.createQuery("FROM Ingredient WHERE ingredientName = :ingredientName", Ingredient.class);
        query.setParameter("ingredientName", name);
        try {
            return query.getSingleResult();
        } catch (NoResultException e) {
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
    public int deleteById(int id) {
        Query query = em.createQuery("DELETE FROM Ingredient i WHERE i.id = :id");
        query.setParameter("id", id);
        return query.executeUpdate();
    }

    @Override
    @Transactional
    public void update(Ingredient ingredient) {
        em.merge(ingredient);
    }
}
