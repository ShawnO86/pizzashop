package com.pizzashop.dao;

import com.pizzashop.entities.Order;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.TypedQuery;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Repository;

@Repository
public class OrderDAOImpl implements OrderDAO {
    EntityManager em;

    public OrderDAOImpl(EntityManager em) {
        this.em = em;
    }

    @Override
    @Transactional
    public Integer save(Order order) {
        em.persist(order);
        return order.getId();
    }

    @Override
    public Order findById(Integer id) {
        try {
            return em.find(Order.class, id);
        } catch (NoResultException e) {
            return null;
        }
    }

    @Override
    public Order findByUsername(String username) {
        TypedQuery<Order> query = em.createQuery("FROM Order o WHERE o.user.username = :username AND o.is_complete = false", Order.class);
        query.setParameter("username", username);
        try {
            return query.getSingleResult();
        } catch (NoResultException e) {
            return null;
        }
    }

}
