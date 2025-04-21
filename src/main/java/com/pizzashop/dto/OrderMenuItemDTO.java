package com.pizzashop.dto;

public class OrderMenuItemDTO {
    private Integer menuItemID;
    private String menuItemName;
    private int menuItemAmount;

    public OrderMenuItemDTO() {}

    public Integer getMenuItemID() {
        return menuItemID;
    }
    public void setMenuItemID(Integer menuItemID) {
        this.menuItemID = menuItemID;
    }
    public String getMenuItemName() {
        return menuItemName;
    }
    public void setMenuItemName(String menuItemName) {
        this.menuItemName = menuItemName;
    }
    public int getMenuItemAmount() {
        return menuItemAmount;
    }
    public void setMenuItemAmount(int menuItemAmount) {
        this.menuItemAmount = menuItemAmount;
    }
}
