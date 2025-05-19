package com.pizzashop.dao;

import com.pizzashop.entities.Order;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.TypedQuery;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class OrderDAOImpl implements OrderDAO {
    EntityManager em;

    public OrderDAOImpl(EntityManager em) {
        this.em = em;
    }

    @Override
    @Transactional
    public Order save(Order order) {
        em.persist(order);
        return order;
    }

    @Override
    public Order findById(int id) {
        try {
            return em.find(Order.class, id);
        } catch (NoResultException e) {
            return null;
        }
    }

    @Override
    public Order findByUsername(String username) {
        TypedQuery<Order> query = em.createQuery("SELECT o FROM Order o " +
                "WHERE o.user.username = :username AND o.is_complete = false", Order.class);
        query.setParameter("username", username);
        try {
            return query.getSingleResult();
        } catch (NoResultException e) {
            return null;
        }
    }

    @Override
    public List<Order> findAllIncomplete() {
        TypedQuery<Order> query = em.createQuery("SELECT o FROM Order o " +
                "JOIN FETCH o.orderMenuItems Omi " +
                "LEFT JOIN FETCH Omi.menuItem LEFT JOIN FETCH Omi.customPizza " +
                "WHERE o.is_complete = false", Order.class);
        return query.getResultList();
    }

    @Override
    @Transactional
    public void update(Order order) {
        em.merge(order);
    }

}
