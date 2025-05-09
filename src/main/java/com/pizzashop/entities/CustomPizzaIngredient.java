package com.pizzashop.entities;

import jakarta.persistence.*;

@Entity
@Table(name = "custompizzas_ingredients")
public class CustomPizzaIngredient {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private int id;

    @ManyToOne
    @JoinColumn(name = "custom_pizza_id")
    private CustomPizza customPizza;

    @ManyToOne
    @JoinColumn(name = "ingredient_id")
    private Ingredient ingredient;

    @Column(name = "quantity_used")
    private int quantityUsed;

    @Transient
    private boolean isExtra;

    public CustomPizzaIngredient() {}

    public CustomPizzaIngredient(CustomPizza customPizza, Ingredient ingredient, boolean isExtra) {
        this.customPizza = customPizza;
        this.ingredient = ingredient;
        this.isExtra = isExtra;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public CustomPizza getMenuItem() {
        return customPizza;
    }

    public void setMenuItem(CustomPizza customPizza) {
        this.customPizza = customPizza;
    }

    public Ingredient getIngredient() {
        return ingredient;
    }

    public void setIngredient(Ingredient ingredient) {
        this.ingredient = ingredient;
    }

    public int getQuantityUsed() {
        return quantityUsed;
    }

    public void setQuantityUsed(int quantityUsed) {
        this.quantityUsed = quantityUsed;
    }

    public boolean getIsExtra() {
        return isExtra;
    }

    public void setIsExtra(boolean isExtra) {
        this.isExtra = isExtra;
    }

    public void setQuantityUsedBySize(PizzaSizeEnum size) {
        if (this.isExtra) {
            this.quantityUsed = size.getExtraIngredientAmount();
        } else {
            this.quantityUsed = size.getIngredientAmount();
        }
    }
}