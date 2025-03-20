package com.pizzashop.entities;

import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "menuitem")
public class MenuItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;

    @Column(name = "dish_name")
    private String dishName;

    @Column(name = "description")
    private String description;

    @Column(name = "price_cents")
    private int priceCents;

    @Enumerated(EnumType.STRING)
    @Column(name = "category")
    private MenuCategoryEnum menuCategory;

    @Column(name = "available")
    private Boolean isAvailable;

    @ManyToMany(mappedBy = "menuItems", fetch = FetchType.LAZY,
            cascade = {CascadeType.DETACH, CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH})
    private List<Order> orders;

    @OneToMany(mappedBy = "menuItem",
            cascade = {CascadeType.ALL})
    private List<MenuItemIngredient> menuItemIngredients;


    public MenuItem() {}

    public MenuItem(String dishName, String description, MenuCategoryEnum menuCategory, Boolean isAvailable) {
        this.dishName = dishName;
        this.description = description;
        this.menuCategory = menuCategory;
        this.isAvailable = isAvailable;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
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

    public MenuCategoryEnum getMenuCategory() {
        return menuCategory;
    }

    public void setMenuCategory(MenuCategoryEnum menuCategory) {
        this.menuCategory = menuCategory;
    }

    public Boolean getIsAvailable() {
        return isAvailable;
    }

    public void setIsAvailable(Boolean isAvailable) {
        this.isAvailable = isAvailable;
    }

    public List<Order> getOrders() {
        return orders;
    }

    public void setOrders(List<Order> orders) {
        this.orders = orders;
    }

    public List<MenuItemIngredient> getMenuItemIngredients() {
        return menuItemIngredients;
    }

    public void setMenuItemIngredients(List<MenuItemIngredient> menuItemIngredients) {
        this.menuItemIngredients = menuItemIngredients;
    }

    public void addIngredient(MenuItemIngredient menuItemIngredient) {
        if (menuItemIngredients == null) {
            menuItemIngredients = new ArrayList<>();
        }
        menuItemIngredients.add(menuItemIngredient);
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
