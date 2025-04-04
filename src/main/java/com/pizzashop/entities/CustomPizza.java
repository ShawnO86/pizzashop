package com.pizzashop.entities;

import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "custom_pizza")
public class CustomPizza {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Enumerated(EnumType.STRING)
    @Column(name = "size")
    private PizzaSizeEnum size;

    @Column(name = "price_cents")
    private int priceCents;

    @OneToMany(mappedBy = "customPizza",
            cascade = {CascadeType.ALL}, fetch = FetchType.LAZY)
    private List<CustomPizzaIngredient> customPizzaIngredients;

    public CustomPizza() {}

    public CustomPizza(PizzaSizeEnum size, int priceCents) {
        this.size = size;
        this.priceCents = priceCents;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public PizzaSizeEnum getSize() {
        return size;
    }

    public void setSize(PizzaSizeEnum size) {
        this.size = size;
    }

    public int getPriceCents() {
        return priceCents;
    }

    public void setPriceCents(int priceCents) {
        this.priceCents = priceCents;
    }

    public List<CustomPizzaIngredient> getCustomPizzaIngredients() {
        return customPizzaIngredients;
    }

    public void setCustomPizzaIngredients(List<CustomPizzaIngredient> customPizzaIngredients) {
        this.customPizzaIngredients = customPizzaIngredients;
    }

    public void addIngredient(CustomPizzaIngredient ingredient) {
        if (customPizzaIngredients == null) {
            customPizzaIngredients = new ArrayList<>();
        }
        customPizzaIngredients.add(ingredient);
    }

    @Override
    public String toString() {
        return "CustomPizza{" +
                "size=" + size +
                ", priceCents=" + priceCents +
                '}';
    }
}
