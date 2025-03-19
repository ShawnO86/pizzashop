package com.pizzashop.dao;

import com.pizzashop.entities.MenuItemIngredient;
import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;
import jakarta.persistence.TypedQuery;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class MenuItemIngredientDAOImpl implements MenuItemIngredientDAO {

    EntityManager em;

    public MenuItemIngredientDAOImpl(EntityManager em) {
        this.em = em;
    }

    @Override
    public List<MenuItemIngredient> findAll() {
        TypedQuery<MenuItemIngredient> query = em.createQuery("FROM MenuItemIngredient", MenuItemIngredient.class);
        return query.getResultList();
    }

    @Override
    public List<MenuItemIngredient> findAllByMenuItemId(Integer menuItemId) {
        TypedQuery<MenuItemIngredient> query = em.createQuery("FROM MenuItemIngredient m WHERE m.menuItem.id = :menuItemId", MenuItemIngredient.class);
        query.setParameter("menuItemId", menuItemId);
        return query.getResultList();
    }

    @Override
    public MenuItemIngredient findById(Integer id) {
        return em.find(MenuItemIngredient.class, id);
    }

    @Override
    public void deleteByMenuItemId(Integer menuItemId) {
        Query query = em.createQuery("DELETE FROM MenuItemIngredient m WHERE m.menuItem.id = :menuItemId");
        query.setParameter("menuItemId", menuItemId);
        query.executeUpdate();
    }
}
