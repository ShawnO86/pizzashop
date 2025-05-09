package com.pizzashop.dao;

import com.pizzashop.entities.Order;
import jakarta.persistence.EntityManager;
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
        return em.find(Order.class, id);
    }

}
