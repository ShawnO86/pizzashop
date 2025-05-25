package com.pizzashop.dto;

import java.util.List;

public class CustomPizzaDTO {
    private String pizzaName;
    private List<ToppingDTO> toppings;
    private List<ToppingDTO> extraToppings;
    private SizeDTO pizzaSize;
    private int basePizzaID;
    private int quantity;
    private int pricePerPizza;
    private int totalPizzaPrice;
    private Integer customPizzaID;

    public CustomPizzaDTO() {

    }

    public CustomPizzaDTO(String pizzaName, int quantity) {
        this.pizzaName = pizzaName;
        this.quantity = quantity;
    }

    public CustomPizzaDTO(String pizzaName, List<ToppingDTO> toppings, List<ToppingDTO> extraToppings, SizeDTO pizzaSize, int quantity) {
        this.pizzaName = pizzaName;
        this.toppings = toppings;
        this.extraToppings = extraToppings;
        this.pizzaSize = pizzaSize;
        this.quantity = quantity;
    }

    public String getPizzaName() {
        return pizzaName;
    }
    public void setPizzaName(String pizzaName) {
        this.pizzaName = pizzaName;
    }
    public List<ToppingDTO> getToppings() {
        return toppings;
    }
    public void setToppings(List<ToppingDTO> toppings) {
        this.toppings = toppings;
    }
    public List<ToppingDTO> getExtraToppings() {
        return extraToppings;
    }
    public void setExtraToppings(List<ToppingDTO> extraToppings) {
        this.extraToppings = extraToppings;
    }
    public SizeDTO getPizzaSize() {
        return pizzaSize;
    }
    public void setPizzaSize(SizeDTO pizzaSize) {
        this.pizzaSize = pizzaSize;
    }
    public int getBasePizzaId() {
        return basePizzaID;
    }
    public void setBasePizzaId(int basePizzaID) {
        this.basePizzaID = basePizzaID;
    }
    public int getQuantity() {
        return quantity;
    }
    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }
    public int getPricePerPizza() {
        return pricePerPizza;
    }
    public void setPricePerPizza(int pricePerPizza) {
        this.pricePerPizza = pricePerPizza;
    }
    public int getTotalPizzaPrice() {
        return totalPizzaPrice;
    }
    public void setTotalPizzaPrice(int totalPizzaPrice) {
        this.totalPizzaPrice = totalPizzaPrice;
    }
    public Integer getCustomPizzaID() {
        return customPizzaID;
    }
    public void setCustomPizzaID(Integer customPizzaID) {
        this.customPizzaID = customPizzaID;
    }

    @Override
    public String toString() {
        return "CustomPizzaDTO{" +
                "pizzaName='" + pizzaName + '\'' +
                ", toppings=" + toppings +
                ", extraToppings=" + extraToppings +
                ", pizzaSize=" + pizzaSize +
                ", quantity=" + quantity +
                ", pricePerPizza=" + pricePerPizza +
                ", totalPizzaPrice=" + totalPizzaPrice +
                '}';
    }
}