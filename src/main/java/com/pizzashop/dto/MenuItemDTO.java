package com.pizzashop.dto;

import com.pizzashop.entities.MenuCategoryEnum;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.List;

public class MenuItemDTO {
    @NotNull(message = "required")
    @Size(min = 3, message = "minimum of 3 characters required")
    private String dishName;

    @NotNull(message = "required")
    @Size(min = 4, message = "minimum of 4 characters required")
    private String description;

    @NotNull(message = "required")
    private MenuCategoryEnum menuCategory;

    private boolean isAvailable;

    private List<int[]> ingredientIdAmounts;

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

    public boolean getIsAvailable() {
        return isAvailable;
    }

    public void setIsAvailable(boolean isAvailable) {
        this.isAvailable = isAvailable;
    }

    public List<int[]> getIngredientIdAmounts() {
        return ingredientIdAmounts;
    }

    public void setIngredientIdAmounts(List<int[]> ingredientIdAmounts) {
        this.ingredientIdAmounts = ingredientIdAmounts;
    }

    @Override
    public String toString() {
        return "MenuItemDTO{" +
                "dishName='" + dishName + '\'' +
                ", description='" + description + '\'' +
                ", menuCategory=" + menuCategory +
                '}';
    }
}
