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

    @Column(name = "is_pizza_ingredient")
    private boolean isPizzaTopping;

    @Column(name = "markup_multi")
    private Integer markupMulti;

    @OneToMany(mappedBy = "ingredient", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<MenuItemIngredient> menuItemIngredients;

    @OneToMany(mappedBy = "ingredient", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<CustomPizzaIngredient> customPizzaIngredients;

    @Transient
    private int centsPricePer;

    public Ingredient() {}

    public Ingredient(String ingredientName, int currentStock, int centsCostPer, Integer markupMulti) {
        this.ingredientName = ingredientName;
        this.currentStock = currentStock;
        this.centsCostPer = centsCostPer;
        this.unitOfMeasure = "ounces";
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

    public int getCentsCostPer() {
        return centsCostPer;
    }

    public void setCentsCostPer(int centsCostPer) {
        this.centsCostPer = centsCostPer;
    }

    public int getCentsPricePer() {
        return this.centsCostPer * this.markupMulti;
    }

    public boolean getIsPizzaTopping() {
        return isPizzaTopping;
    }

    public void setIsPizzaTopping(boolean pizzaIngredient) {
        isPizzaTopping = pizzaIngredient;
    }

    public Integer getMarkupMulti() {
        return markupMulti;
    }

    public void setMarkupMulti(Integer markupMulti) {
        this.markupMulti = markupMulti;
    }

    public List<MenuItemIngredient> getMenuItemIngredients() {
        return menuItemIngredients;
    }

    public void setMenuItemIngredients(List<MenuItemIngredient> menuItemIngredients) {
        this.menuItemIngredients = menuItemIngredients;
    }

    public List<CustomPizzaIngredient> getCustomPizzaIngredients() {
        return customPizzaIngredients;
    }

    public void setCustomPizzaIngredients(List<CustomPizzaIngredient> customPizzaIngredients) {
        this.customPizzaIngredients = customPizzaIngredients;
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
