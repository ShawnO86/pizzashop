package com.pizzashop.dto;

import com.pizzashop.entities.PizzaSizeEnum;

import java.util.Map;

public class CustomPizzaDTO {

    private String pizzaName;
    // name, id
    private Map<String, Integer> toppings;
    private Map<String, Integer> extraToppings;
    private PizzaSizeEnum pizzaSize;
    private int quantity;

    public CustomPizzaDTO() {}

    public String getPizzaName() {
        return pizzaName;
    }
    public void setPizzaName(String pizzaName) {
        this.pizzaName = pizzaName;
    }
    public Map<String, Integer> getToppings() {
        return toppings;
    }
    public void setToppings(Map<String, Integer> toppings) {
        this.toppings = toppings;
    }
    public Map<String, Integer> getExtraToppings() {
        return extraToppings;
    }
    public void setExtraToppings(Map<String, Integer> extraToppings) {
        this.extraToppings = extraToppings;
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
