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
    private boolean extra;

    public CustomPizzaIngredient() {}

    public CustomPizzaIngredient(CustomPizza customPizza, Ingredient ingredient, boolean extra) {
        this.customPizza = customPizza;
        this.ingredient = ingredient;
        this.extra = extra;
        this.setQuantityBySize(PizzaSizeEnum.SMALL);
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

    public boolean getExtra() {
        return extra;
    }

    public void setExtra(boolean extra) {
        this.extra = extra;
        if (this.extra) {
            this.quantityUsed = 4;
        } else {
            this.quantityUsed = 6;
        }
    }

    public void setQuantityBySize(PizzaSizeEnum size) {
        int additional = 0;
        if (this.extra) {
            additional = 2;
        }
        switch (size) {
            case SMALL:
                this.quantityUsed = 2 + additional;
                break;
            case MEDIUM:
                this.quantityUsed = 4 + additional;
                break;
            case LARGE:
                this.quantityUsed = 6 + additional;
                break;
        }
    }

}
