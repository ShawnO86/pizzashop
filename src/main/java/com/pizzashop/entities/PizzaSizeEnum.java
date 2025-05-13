package com.pizzashop.entities;

public enum PizzaSizeEnum {
    SMALL(2, 1, "8in. 6 slices", "Small Cheese Pizza"),
    MEDIUM(4, 2, "12in. 8 slices", "Medium Cheese Pizza"),
    LARGE(6, 3, "16in. 10 slices", "Large Cheese Pizza"),;

    private final int ingredientAmount;
    private final int extraIngredientAmount;
    private final String description;
    private final String pizzaName;

    PizzaSizeEnum(int ingredientAmount, int extraIngredientAmount, String description, String pizzaName) {
        this.ingredientAmount = ingredientAmount;
        this.extraIngredientAmount = extraIngredientAmount;
        this.description = description;
        this.pizzaName = pizzaName;
    }

    public int getIngredientAmount() {
        return ingredientAmount;
    }

    public int getExtraIngredientAmount() {
        return extraIngredientAmount;
    }

    public String getDescription() {
        return description;
    }

    public String getPizzaName() {
        return pizzaName;
    }
}
