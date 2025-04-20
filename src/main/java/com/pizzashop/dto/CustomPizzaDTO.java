package com.pizzashop.dto;

import com.pizzashop.entities.PizzaSizeEnum;

import java.util.List;

public class CustomPizzaDTO {

    private String pizzaName;
    private List<Integer> toppingIds;
    private List<String> toppingNames;
    private List<Integer> extraToppingIds;
    private List<String> extraToppingNames;
    private PizzaSizeEnum pizzaSize;
    private int quantity;

    public CustomPizzaDTO() {}

    public String getPizzaName() {
        return pizzaName;
    }
    public void setPizzaName(String pizzaName) {
        this.pizzaName = pizzaName;
    }
    public List<Integer> getToppingIds() {
        return toppingIds;
    }
    public void setToppingIds(List<Integer> toppingIds) {
        this.toppingIds = toppingIds;
    }
    public List<String> getToppingNames() {
        return toppingNames;
    }
    public void setToppingNames(List<String> toppingNames) {
        this.toppingNames = toppingNames;
    }
    public List<Integer> getExtraToppingIds() {
        return extraToppingIds;
    }
    public void setExtraToppingIds(List<Integer> extraToppingIds) {
        this.extraToppingIds = extraToppingIds;
    }
    public List<String> getExtraToppingNames() {
        return extraToppingNames;
    }
    public void setExtraToppingNames(List<String> extraToppingNames) {
        this.extraToppingNames = extraToppingNames;
    }
    public PizzaSizeEnum getPizzaSize() {
        return pizzaSize;
    }
    public void setPizzaSize(PizzaSizeEnum pizzaSize) {
        this.pizzaSize = pizzaSize;
    }
    public int getQuantity() {
        return quantity;
    }
    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }
}
