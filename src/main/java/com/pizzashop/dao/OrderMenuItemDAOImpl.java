package com.pizzashop.dao;

import com.pizzashop.entities.OrderMenuItem;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class OrderMenuItemDAOImpl implements OrderMenuItemDAO {
    EntityManager em;

    @Autowired
    public OrderMenuItemDAOImpl(EntityManager em) {
        this.em = em;
    }

    @Override
    public List<OrderMenuItem> findAll() {
        TypedQuery<OrderMenuItem> query = em.createQuery("FROM OrderMenuItem", OrderMenuItem.class);
        return query.getResultList();
    }

    @Override
    public List<OrderMenuItem> findAllByMenuItemId(Integer menuItemId) {
        TypedQuery<OrderMenuItem> query = em.createQuery("FROM OrderMenuItem m WHERE m.menuItem.id = :menuItemId", OrderMenuItem.class);
        query.setParameter("menuItemId", menuItemId);
        return query.getResultList();
    }
}
