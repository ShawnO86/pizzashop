package com.pizzashop.dao;

import com.pizzashop.entities.Role;
import com.pizzashop.entities.RoleEnum;
import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;
import jakarta.persistence.TypedQuery;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class RoleDAOImpl implements RoleDAO {

    private final EntityManager em;

    public RoleDAOImpl(EntityManager em) {
        this.em = em;
    }

    @Override
    public Role findByRole(RoleEnum role) {
        TypedQuery<Role> query = em.createQuery("SELECT r FROM Role r WHERE r.role = :role", Role.class);
        query.setParameter("role", role);
        return query.getSingleResult();
    }

    @Override
    public List<Role> findAll() {
        TypedQuery<Role> query = em.createQuery("SELECT r FROM Role r", Role.class);
        return query.getResultList();
    }

    @Override
    @Transactional
    public void save(Role role) {
        em.persist(role);
    }

    @Override
    @Transactional
    public void deleteAll() {
        Query query = em.createQuery("DELETE FROM Role");
        query.executeUpdate();
    }
}
