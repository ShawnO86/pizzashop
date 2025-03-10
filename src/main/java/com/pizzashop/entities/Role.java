package com.pizzashop.entities;

import jakarta.persistence.*;

@Entity
@Table(name = "role")
public class Role {

    @Id
    @Column(name = "id")
    private int id;

    @Enumerated(EnumType.STRING)
    @Column(name = "role")
    private RoleEnum role;

    //Todo: setup many-to-many relation to User class

    public Role() {}

    public Role(RoleEnum role) {
        this.role = role;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public RoleEnum getRole() {
        return role;
    }

    public void setRole(RoleEnum role) {
        this.role = role;
    }

}
