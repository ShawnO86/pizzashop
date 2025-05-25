package com.pizzashop.dao;

import com.pizzashop.entities.Order;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.TypedQuery;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
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
    public Order findByIdJoinFetchUserDetails(int id) {
        TypedQuery<Order> query = em.createQuery("FROM Order o " +
                "JOIN FETCH o.orderMenuItems Omi JOIN FETCH o.user u JOIN FETCH u.userDetail " +
                "LEFT JOIN FETCH Omi.menuItem LEFT JOIN FETCH Omi.customPizza " +
                "WHERE o.id = :id", Order.class);
        query.setParameter("id", id);
        try {
            return query.getSingleResult();
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
    public List<Order> findAllIncompleteJoinFetchUserDetails() {
        TypedQuery<Order> query = em.createQuery("SELECT o FROM Order o " +
                "JOIN FETCH o.orderMenuItems Omi JOIN FETCH o.user u JOIN FETCH u.userDetail " +
                "LEFT JOIN FETCH Omi.menuItem mi LEFT JOIN FETCH Omi.customPizza cp " +
                "WHERE o.is_complete = false", Order.class);
        return query.getResultList();
    }

    @Override
    public List<Order> findAllByDateRange(LocalDate from, LocalDate to) {
        // Adds time to date input (00:00 to 23:59)
        LocalDateTime startOfDay = from.atStartOfDay();
        LocalDateTime endOfDay = to.atTime(LocalTime.MAX);

        TypedQuery<Order> query = em.createQuery("SELECT o FROM Order o " +
                "JOIN FETCH o.orderMenuItems omi " +
                "LEFT JOIN FETCH omi.menuItem mi LEFT JOIN FETCH omi.customPizza cp " +
                "WHERE o.order_date BETWEEN :startDateTime AND :endDateTime " +
                "AND o.is_complete = true", Order.class);

        query.setParameter("startDateTime", startOfDay);
        query.setParameter("endDateTime", endOfDay);

        return query.getResultList();
    }

    @Override
    public List<Order> findAllFulfilledByIdInDateRange(LocalDate from, LocalDate to, String username) {
        LocalDateTime startOfDay = from.atStartOfDay();
        LocalDateTime endOfDay = to.atTime(LocalTime.MAX);

        TypedQuery<Order> query = em.createQuery("SELECT o FROM Order o " +
                "JOIN FETCH o.orderMenuItems omi " +
                "LEFT JOIN FETCH omi.menuItem mi LEFT JOIN FETCH omi.customPizza cp " +
                "WHERE o.order_date BETWEEN :startDateTime AND :endDateTime " +
                "AND o.fulfilled_by = :username " +
                "AND o.is_complete = true", Order.class);

        query.setParameter("startDateTime", startOfDay);
        query.setParameter("endDateTime", endOfDay);
        query.setParameter("username", username);

        return query.getResultList();
    }

    @Override
    @Transactional
    public void update(Order order) {
        em.merge(order);
    }

}
