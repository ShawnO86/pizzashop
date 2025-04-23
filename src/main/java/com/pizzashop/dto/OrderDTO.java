package com.pizzashop.dto;

import java.util.List;

public class OrderDTO {
    // todo : figure out constraints for nested DTO?
    private List<OrderMenuItemDTO> menuItemList;
    private List<CustomPizzaDTO> customPizzaList;

    public OrderDTO() {}

    public List<OrderMenuItemDTO> getMenuItemList() {
        return menuItemList;
    }
    public void setMenuItemList(List<OrderMenuItemDTO> menuItemList) {
        this.menuItemList = menuItemList;
    }
    public List<CustomPizzaDTO> getCustomPizzaList() {
        return customPizzaList;
    }
    public void setCustomPizzaList(List<CustomPizzaDTO> customPizzaList) {
        this.customPizzaList = customPizzaList;
    }
}
