package com.pizzashop.dto;

import com.pizzashop.entities.PizzaSizeEnum;

import java.util.List;

public class CustomPizzaDTO {

    private String pizzaName;
    private List<Integer> toppings;
    private List<Integer> extraToppings;
    private PizzaSizeEnum pizzaSize;
    private int quantity;

    public CustomPizzaDTO() {}

    public String getPizzaName() {
        return pizzaName;
    }
    public void setPizzaName(String pizzaName) {
        this.pizzaName = pizzaName;
    }
    public List<Integer> getToppings() {
        return toppings;
    }
    public void setToppings(List<Integer> toppings) {
        this.toppings = toppings;
    }
    public List<Integer> getExtraToppings() {
        return extraToppings;
    }
    public void setExtraToppings(List<Integer> extraToppings) {
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

    @Override
    public String toString() {
        return "CustomPizzaDTO{" +
                "pizzaName='" + pizzaName + '\n' +
                "toppings=" + toppings + '\n' +
                "extraToppings=" + extraToppings + '\n' +
                "pizzaSize=" + pizzaSize +
                ", quantity=" + quantity +
                '}';
    }
}
