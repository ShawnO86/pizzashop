package com.pizzashop.dto;

import java.util.List;

public class OrderDTO {

    private List<int[]> menuItemsNamesAndQuantity;

    public OrderDTO() {}

    public List<int[]> getMenuItemsNamesAndQuantity() {
        return menuItemsNamesAndQuantity;
    }

    public void setMenuItemsNames(List<int[]> menuItemsNamesAndQuantity) {
        this.menuItemsNamesAndQuantity = menuItemsNamesAndQuantity;
    }

    @Override
    public String toString() {
        return "OrderDTO{" +
                "menuItems=" + menuItemsNamesAndQuantity +
                '}';
    }
}
