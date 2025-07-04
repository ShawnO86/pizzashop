package com.pizzashop.entities;

import jakarta.persistence.*;

@Entity
@Table(name = "menuitems_ingredients")
public class MenuItemIngredient {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "menu_item_id")
    private MenuItem menuItem;

    @ManyToOne
    @JoinColumn(name = "ingredient_id")
    private Ingredient ingredient;

    @Column(name = "quantity_used")
    private int quantityUsed;

    public MenuItemIngredient() {}

    public MenuItemIngredient(MenuItem menuItem, Ingredient ingredient, int quantityUsed) {
        this.menuItem = menuItem;
        this.ingredient = ingredient;
        this.quantityUsed = quantityUsed;
    }

    public int getId() {
        return id;
    }

    public MenuItem getMenuItem() {
        return menuItem;
    }

    public Ingredient getIngredient() {
        return ingredient;
    }

    public int getQuantityUsed() {
        return quantityUsed;
    }

    @Override
    public String toString() {
        return "MenuItemIngredient{" +
                "menuItem=" + menuItem +
                ", ingredient=" + ingredient +
                ", quantityUsed=" + quantityUsed +
                '}';
    }
}
