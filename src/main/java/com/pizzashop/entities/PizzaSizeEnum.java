package com.pizzashop.entities;

public enum PizzaSizeEnum {
    SMALL(2, 1, "8in. 6 slices"),
    MEDIUM(4, 2, "12in. 8 slices"),
    LARGE(6, 3, "16in. 10 slices");

    private final int ingredientAmount;
    private final int extraIngredientAmount;
    private final String description;

    PizzaSizeEnum(int ingredientAmount, int extraIngredientAmount, String description) {
        this.ingredientAmount = ingredientAmount;
        this.extraIngredientAmount = extraIngredientAmount;
        this.description = description;
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
}
