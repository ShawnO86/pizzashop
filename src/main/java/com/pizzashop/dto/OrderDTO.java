package com.pizzashop.dto;

import java.util.ArrayList;
import java.util.List;

public class OrderDTO {
    private List<OrderMenuItemDTO> menuItemList;
    private List<CustomPizzaDTO> customPizzaList;

    private int totalPrice;

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
    public int getTotalPrice() {
        return totalPrice;
    }
    public void setTotalPrice(int totalPrice) {
        this.totalPrice = totalPrice;
    }

    public void addMenuItem(OrderMenuItemDTO menuItem) {
        if (menuItemList == null) {
            menuItemList = new ArrayList<>();
        }
        menuItemList.add(menuItem);
    }

    public void addCustomPizza(CustomPizzaDTO customPizza) {
        if (customPizzaList == null) {
            customPizzaList = new ArrayList<>();
        }
        customPizzaList.add(customPizza);
    }

    @Override
    public String toString() {
        return "OrderDTO{" +
                "menuItemList=" + menuItemList +
                ", customPizzaList=" + customPizzaList +
                ", totalPrice=" + totalPrice +
                '}';
    }
}