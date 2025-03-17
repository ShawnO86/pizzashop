package com.pizzashop.dao;

import com.pizzashop.entities.Ingredient;
import com.pizzashop.entities.MenuItem;
import com.pizzashop.entities.MenuItemIngredient;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.TypedQuery;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
public class MenuItemDAOImpl implements MenuItemDAO {
    EntityManager em;

    @Autowired
    public MenuItemDAOImpl(EntityManager em) {
        this.em = em;
    }

    @Override
    public MenuItem findByName(String name) {
        TypedQuery<MenuItem> query = em.createQuery("FROM MenuItem WHERE dishName = :dishName", MenuItem.class);
        query.setParameter("dishName", name);

        try {
            return query.getSingleResult();
        } catch (NoResultException e) {
            System.out.println("No dish found by name: " + name);
            return null;
        }
    }

    @Override
    public List<MenuItem> findAll() {
        TypedQuery<MenuItem> query = em.createQuery("FROM MenuItem", MenuItem.class);

        return query.getResultList();
    }

    @Override
    @Transactional
    public void save(MenuItem menuItem) {
        em.persist(menuItem);
    }

    @Override
    @Transactional
    public void delete(MenuItem menuItem) {
        em.remove(menuItem);
    }

    @Override
    @Transactional
    public void update(MenuItem menuItem) {
        em.merge(menuItem);
    }
}
