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
        this.setQuantityBySize(customPizza.getSize());
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
    }

    public void setQuantityBySize(PizzaSizeEnum size) {
        int additional = 0;

        switch (size) {
            case PizzaSizeEnum.SMALL:
                if (this.extra) {
                    additional = PizzaSizeEnum.SMALL.getIngredientAmount() / 2;
                }
                this.quantityUsed = PizzaSizeEnum.SMALL.getIngredientAmount() + additional;
                break;
            case PizzaSizeEnum.MEDIUM:
                if (this.extra) {
                    additional = PizzaSizeEnum.MEDIUM.getIngredientAmount() / 2;
                }
                this.quantityUsed = PizzaSizeEnum.MEDIUM.getIngredientAmount() + additional;
                break;
            case PizzaSizeEnum.LARGE:
                if (this.extra) {
                    additional = PizzaSizeEnum.LARGE.getIngredientAmount() / 2;
                }
                this.quantityUsed = PizzaSizeEnum.LARGE.getIngredientAmount() + additional;
                break;
        }
    }
}