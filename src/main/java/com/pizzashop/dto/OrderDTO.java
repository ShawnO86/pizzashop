package com.pizzashop.dto;

import com.pizzashop.entities.MenuItem;
import jakarta.validation.constraints.Size;

import java.util.ArrayList;
import java.util.List;

public class OrderDTO {

    @Size.List(
            @Size(min = 1, message = "Must have at least one item"))
    private List<MenuItem> menuItems;

    public OrderDTO() {}

    public List<MenuItem> getMenuItems() {
        return menuItems;
    }

    public void setMenuItems(List<MenuItem> menuItems) {
        this.menuItems = menuItems;
    }

    public void addMenuItem(MenuItem menuItem) {
        if (menuItems == null) {
            menuItems = new ArrayList<>();
        }
        menuItems.add(menuItem);
    }

    @Override
    public String toString() {
        return "OrderDTO{" +
                "menuItems=" + menuItems +
                '}';
    }
}
