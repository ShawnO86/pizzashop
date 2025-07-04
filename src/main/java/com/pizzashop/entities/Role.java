package com.pizzashop.entities;

import jakarta.persistence.*;

@Entity
@Table(name = "role")
public class Role {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;

    @Enumerated(EnumType.STRING)
    @Column(name = "role")
    private RoleEnum role;

    public Role() {}

    public Role(RoleEnum role) {
        this.role = role;
    }

    public int getId() {
        return id;
    }

    public RoleEnum getRole() {
        return role;
    }

    public void setRole(RoleEnum role) {
        this.role = role;
    }

    @Override
    public String toString() {
        return "Role{" +
                "role=" + role +
                '}';
    }
}
