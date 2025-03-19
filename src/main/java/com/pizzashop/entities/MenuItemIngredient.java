package com.pizzashop.entities;

import jakarta.persistence.*;

import java.util.Objects;

@Entity
@Table(name = "menuitems_ingredients", uniqueConstraints = @UniqueConstraint(columnNames = {"menu_item_id", "ingredient_id"}))
public class MenuItemIngredient {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private int id;

    @ManyToOne
    @JoinColumn(name = "menu_item_id")
    private MenuItem menuItem;

    @ManyToOne
    @JoinColumn(name = "ingredient_id")
    private Ingredient ingredient;

    @Column(name = "quantity_used")
    private Integer quantityUsed;

    public MenuItemIngredient() {}

    public MenuItemIngredient(MenuItem menuItem, Ingredient ingredient, int quantityUsed) {
        this.menuItem = menuItem;
        this.ingredient = ingredient;
        this.quantityUsed = quantityUsed;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public MenuItem getMenuItem() {
        return menuItem;
    }

    public void setMenuItem(MenuItem menuItem) {
        this.menuItem = menuItem;
    }

    public Ingredient getIngredient() {
        return ingredient;
    }

    public void setIngredient(Ingredient ingredient) {
        this.ingredient = ingredient;
    }

    public Integer getQuantityUsed() {
        return quantityUsed;
    }

    public void setQuantityUsed(Integer quantityUsed) {
        this.quantityUsed = quantityUsed;
    }

    @Override
    public String toString() {
        return "MenuItemIngredient{" +
                "menuItem=" + menuItem +
                ", ingredient=" + ingredient +
                ", quantityUsed=" + quantityUsed +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        MenuItemIngredient that = (MenuItemIngredient) o;
        return id == that.id && Objects.equals(menuItem, that.menuItem) && Objects.equals(ingredient, that.ingredient) && Objects.equals(quantityUsed, that.quantityUsed);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, menuItem, ingredient, quantityUsed);
    }
}
