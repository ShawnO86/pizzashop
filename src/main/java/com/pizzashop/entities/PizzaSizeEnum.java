package com.pizzashop.entities;

public enum PizzaSizeEnum {
    SMALL(5.50, "8in. 6 slices"),
    MEDIUM(9.00, "12in. 8 slices"),
    LARGE(12.50, "16in. 10 slices");

    private final double price;
    private final String description;

    PizzaSizeEnum(double price, String description) {
        this.price = price;
        this.description = description;
    }

    public double getPrice() {
        return price;
    }

    public String getDescription() {
        return description;
    }
}
