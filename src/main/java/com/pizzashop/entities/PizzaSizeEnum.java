package com.pizzashop.entities;

public enum PizzaSizeEnum {
    SMALL(2, "8in. 6 slices"),
    MEDIUM(4, "12in. 8 slices"),
    LARGE(6,"16in. 10 slices");

    private final int ingredientAmount;
    private final String description;

    PizzaSizeEnum(int ingredientAmount, String description) {
        this.ingredientAmount = ingredientAmount;
        this.description = description;
    }

    public int getIngredientAmount() {
        return ingredientAmount;
    }

    public String getDescription() {
        return description;
    }
}
