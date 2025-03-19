package com.pizzashop.dto;

import com.pizzashop.entities.MenuCategoryEnum;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.Map;

public class MenuItemDTO {

    @NotNull(message = "required")
    @Size(min = 3, message = "minimum of 3 characters required")
    private String dishName;

    @NotNull(message = "required")
    @Size(min = 4, message = "minimum of 4 characters required")
    private String description;

    @NotNull(message = "required")
    private MenuCategoryEnum menuCategory;

    @NotNull(message = "required")
    @Size.List(
            @Size(min = 1, message = "Must have at least one ingredient"))
    private Map<Integer, Integer> ingredientIdAmounts;

    public MenuItemDTO() {}

    public MenuItemDTO(String dishName, String description, MenuCategoryEnum menuCategory) {
        this.dishName = dishName;
        this.description = description;
        this.menuCategory = menuCategory;
    }

    public String getDishName() {
        return dishName;
    }

    public void setDishName(String dishName) {
        this.dishName = dishName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public MenuCategoryEnum getMenuCategory() {
        return menuCategory;
    }

    public void setMenuCategory(MenuCategoryEnum menuCategory) {
        this.menuCategory = menuCategory;
    }

    public Map<Integer, Integer> getIngredientIdAmounts() {
        return ingredientIdAmounts;
    }

    public void setIngredientIdAmounts(Map<Integer, Integer> ingredientIdAmounts) {
        this.ingredientIdAmounts = ingredientIdAmounts;
    }

    @Override
    public String toString() {
        return "MenuItemDTO{" +
                "dishName='" + dishName + '\'' +
                ", description='" + description + '\'' +
                ", menuCategory=" + menuCategory +
                ", menuItemIngredients=" + ingredientIdAmounts +
                '}';
    }
}
