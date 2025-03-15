package com.pizzashop.dao;

import com.pizzashop.entities.User;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Repository;

@Repository
public class UserDAOImpl implements UserDAO {

    private final EntityManager em;

    public UserDAOImpl(EntityManager em) {
        this.em = em;
    }

    @Override
    public User findByUsername(String username) {
        TypedQuery<User> query = em.createQuery("Select u From User u Where u.username = :username", User.class);
        query.setParameter("username", username);
        return query.getSingleResult();
    }

    @Override
    public User findByUsernameJoinFetchDetailsOrders(String username) {
        TypedQuery<User> query = em.createQuery("SELECT u FROM User u " +
                "JOIN FETCH u.userDetail JOIN FETCH u.orders " +
                "WHERE u.username = :username", User.class);
        query.setParameter("username", username);
        return query.getSingleResult();
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
}
