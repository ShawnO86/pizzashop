package com.pizzashop.entities;

import jakarta.persistence.*;

import java.util.List;

@Entity
@Table(name = "custom_pizza")
public class CustomPizza {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "name")
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(name = "size")
    private PizzaSizeEnum size;

    @Column(name = "price_cents")
    private int priceCents;

    @OneToMany(mappedBy = "customPizza",
            cascade = {CascadeType.ALL}, fetch = FetchType.LAZY)
    private List<CustomPizzaIngredient> customPizzaIngredients;

    public CustomPizza() {}

    public CustomPizza(String name, int priceCents, PizzaSizeEnum size) {
        this.name = name;
        this.priceCents = priceCents;
        this.size = size;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public PizzaSizeEnum getSize() {
        return size;
    }

    public int getPriceCents() {
        return priceCents;
    }

    public List<CustomPizzaIngredient> getCustomPizzaIngredients() {
        return customPizzaIngredients;
    }

    public void setCustomPizzaIngredients(List<CustomPizzaIngredient> customPizzaIngredients) {
        this.customPizzaIngredients = customPizzaIngredients;
    }

    @Override
    public String toString() {
        return "CustomPizza{" +
                "size=" + size +
                ", priceCents=" + priceCents +
                '}';
    }
}
