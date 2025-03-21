package com.pizzashop.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class IngredientDTO {
    @NotNull(message = "required")
    @Size(min = 3, message = "minimum of 3 characters required")
    private String ingredientName;

    @NotNull(message = "required")
    @Min(value = 1, message = "minimum of 1")
    private Integer currentStock;

    @NotNull(message = "required")
    @Min(value = 1, message = "minimum of 1")
    private Integer centsCostPer;

    public IngredientDTO() {}

    public String getIngredientName() {
        return ingredientName;
    }

    public void setIngredientName(String ingredientName) {
        this.ingredientName = ingredientName;
    }

    public Integer getCurrentStock() {
        return currentStock;
    }

    public void setCurrentStock(Integer currentStock) {
        this.currentStock = currentStock;
    }

    public Integer getCentsCostPer() {
        return centsCostPer;
    }

    public void setCentsCostPer(Integer centsCostPer) {
        this.centsCostPer = centsCostPer;
    }

    @Override
    public String toString() {
        return "IngredientDTO{" +
                "ingredientName='" + ingredientName + '\'' +
                ", currentStock=" + currentStock +
                ", centsCostPer=" + centsCostPer +
                '}';
    }
}
