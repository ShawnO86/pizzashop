package com.pizzashop.dto;

import java.util.Map;

public class OrderDTO {

    private Map<String, Integer> menuItemsNamesAndQuantity;

    public OrderDTO() {}

    public Map<String, Integer> getMenuItemsNamesAndQuantity() {
        return menuItemsNamesAndQuantity;
    }

    public void setMenuItemsNames(Map<String, Integer> menuItemsNamesAndQuantity) {
        this.menuItemsNamesAndQuantity = menuItemsNamesAndQuantity;
    }

    @Override
    public String toString() {
        return "OrderDTO{" +
                "menuItems=" + menuItemsNamesAndQuantity +
                '}';
    }
}
