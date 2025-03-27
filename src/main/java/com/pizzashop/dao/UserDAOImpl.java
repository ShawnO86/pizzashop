package com.pizzashop.dao;

import com.pizzashop.entities.User;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.TypedQuery;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class UserDAOImpl implements UserDAO {

    private final EntityManager em;

    public UserDAOImpl(EntityManager em) {
        this.em = em;
    }

    @Override
    public User findByUsername(String username) {
        TypedQuery<User> query = em.createQuery("FROM User WHERE username = :username", User.class);
        query.setParameter("username", username);
        try {
            return query.getSingleResult();
        } catch (NoResultException e) {
            System.out.println("No user found by username: " + username);
            return null;
        }
    }

    @Override
    public List<User> findAll() {
        TypedQuery<User> query = em.createQuery("FROM User", User.class);
        return query.getResultList();
    }

    @Override
    public List<User> findAllFetchUserDetails() {
        TypedQuery<User> query = em.createQuery("SELECT u FROM User u " +
                "JOIN FETCH u.userDetail", User.class);
        return query.getResultList();
    }

    @Override
    public List<User> findAllByLastName(String lastName) {
        TypedQuery<User> query = em.createQuery("SELECT u FROM User u " +
                "JOIN FETCH u.userDetail d WHERE d.lastName = :lastName", User.class);
        query.setParameter("lastName", lastName);
        return query.getResultList();
    }

    @Override
    public User findByUsernameJoinFetchOrders(String username) {
        TypedQuery<User> query = em.createQuery(
                "SELECT u FROM User u " +
                "JOIN FETCH u.orders " +
                "WHERE u.username = :username", User.class);
        query.setParameter("username", username);
        try {
            return query.getSingleResult();
        } catch (NoResultException e) {
            System.out.println("No user found by username: " + username);
            return null;
        }
    }

    @Override
    @Transactional
    public void save(User user) {
        em.persist(user);
    }

    @Override
    @Transactional
    public void update(User user) {
        em.merge(user);
    }

    @Override
    public void delete(User user) {
        em.remove(user);
    }

}
