package com.pizzashop.entities;

import jakarta.persistence.*;

import java.util.List;

@Entity
@Table(name = "menuItem")
public class MenuItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private int id;

    @Column(name = "dish_name")
    private String dishName;

    @Column(name = "description")
    private String description;

    @Column(name = "price_cents")
    private int priceCents;

    @ManyToMany(mappedBy = "menuItems", fetch = FetchType.LAZY,
            cascade = {CascadeType.DETACH, CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH})
    private List<Order> orders;

    public MenuItem() {}

    public MenuItem(String dishName, String description, int priceCents) {
        this.dishName = dishName;
        this.description = description;
        this.priceCents = priceCents;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getDishName() {
        return dishName;
    }

    public void setDishName(String dishName) {
        this.dishName = dishName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getPriceCents() {
        return priceCents;
    }

    public void setPriceCents(int priceCents) {
        this.priceCents = priceCents;
    }

    public List<Order> getOrders() {
        return orders;
    }

    public void setOrders(List<Order> orders) {
        this.orders = orders;
    }

    @Override
    public String toString() {
        return "MenuItem{" +
                "dishName='" + dishName + '\'' +
                ", description='" + description + '\'' +
                ", priceCents=" + priceCents +
                '}';
    }
}
