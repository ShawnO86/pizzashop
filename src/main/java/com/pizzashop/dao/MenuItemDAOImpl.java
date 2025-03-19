package com.pizzashop.dao;

import com.pizzashop.entities.MenuItem;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.Query;
import jakarta.persistence.TypedQuery;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class MenuItemDAOImpl implements MenuItemDAO {
    EntityManager em;

    @Autowired
    public MenuItemDAOImpl(EntityManager em) {
        this.em = em;
    }

    @Override
    public MenuItem findByName(String name) {
        TypedQuery<MenuItem> query = em.createQuery("FROM MenuItem m WHERE m.dishName = :dishName", MenuItem.class);
        query.setParameter("dishName", name);

        try {
            return query.getSingleResult();
        } catch (NoResultException e) {
            System.out.println("No dish found by name: " + name);
            return null;
        }
    }

    @Override
    public MenuItem findById(int id) {
        TypedQuery<MenuItem> query = em.createQuery("FROM MenuItem m WHERE m.id = :id", MenuItem.class);
        query.setParameter("id", id);

        try {
            return query.getSingleResult();
        } catch (NoResultException e) {
            System.out.println("No dish found by id: " + id);
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
    public void deleteById(int id) {
        Query query = em.createQuery("DELETE FROM MenuItem m WHERE m.id = :id");
        query.setParameter("id", id);
        query.executeUpdate();
    }

    @Override
    @Transactional
    public void update(MenuItem menuItem) {
        em.merge(menuItem);
    }
}
