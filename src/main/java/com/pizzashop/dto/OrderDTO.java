package com.pizzashop.dto;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class OrderDTO {
    private List<OrderMenuItemDTO> menuItemList;
    private List<CustomPizzaDTO> customPizzaList;
    private int totalPrice;
    private Integer orderID;
    private LocalDateTime orderDateTime;

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
    public Integer getOrderID() {
        return orderID;
    }
    public void setOrderID(Integer orderID) {
        this.orderID = orderID;
    }
    public LocalDateTime getOrderDateTime() {
        return orderDateTime;
    }
    public void setOrderDateTime(LocalDateTime orderDateTime) {
        this.orderDateTime = orderDateTime;
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