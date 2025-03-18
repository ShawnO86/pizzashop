package com.pizzashop.entities;

import jakarta.persistence.*;

import java.util.List;

@Entity
@Table(name = "ingredient")
public class Ingredient {
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "ingredient_name")
    private String ingredientName;

    @Column(name = "current_stock")
    private int currentStock;

    @Column(name = "unit_of_measure")
    private String unitOfMeasure;

    @Column(name = "cents_cost_per")
    private int centsCostPer;

    @OneToMany(mappedBy = "ingredient", cascade = CascadeType.ALL)
    private List<MenuItemIngredient> menuItemIngredients;

    public Ingredient() {}

    public Ingredient(String ingredientName, int currentStock, String unitOfMeasure, int centsCostPer) {
        this.ingredientName = ingredientName;
        this.currentStock = currentStock;
        this.unitOfMeasure = unitOfMeasure;
        this.centsCostPer = centsCostPer;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getIngredientName() {
        return ingredientName;
    }

    public void setIngredientName(String ingredientName) {
        this.ingredientName = ingredientName;
    }

    public int getCurrentStock() {
        return currentStock;
    }

    public void setCurrentStock(int currentStock) {
        this.currentStock = currentStock;
    }

    public String getUnitOfMeasure() {
        return unitOfMeasure;
    }

    public void setUnitOfMeasure(String unitOfMeasure) {
        this.unitOfMeasure = unitOfMeasure;
    }

    public int getCentsCostPer() {
        return centsCostPer;
    }

    public void setCentsCostPer(int centsCostPer) {
        this.centsCostPer = centsCostPer;
    }

    public List<MenuItemIngredient> getMenuItemIngredients() {
        return menuItemIngredients;
    }

    public void setMenuItemIngredients(List<MenuItemIngredient> menuItemIngredients) {
        this.menuItemIngredients = menuItemIngredients;
    }

    @Override
    public String toString() {
        return "Ingredient{" +
                "ingredientName='" + ingredientName + '\'' +
                ", currentStock=" + currentStock +
                ", unitOfMeasure='" + unitOfMeasure + '\'' +
                ", centsCostPer=" + centsCostPer +
                '}';
    }
}
