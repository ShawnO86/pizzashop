package com.pizzashop.dto;

public class OrderMenuItemDTO {
    private Integer menuItemID;
    private String menuItemName;
    private int menuItemAmount;
    private int maxQty;
    private int pricePerItem;

    public OrderMenuItemDTO() {}

    public OrderMenuItemDTO(Integer menuItemID, String menuItemName, int menuItemAmount, int maxQty, int pricePerItem) {
        this.menuItemID = menuItemID;
        this.menuItemName = menuItemName;
        this.menuItemAmount = menuItemAmount;
        this.maxQty = maxQty;
        this.pricePerItem = pricePerItem;
    }

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
    public int getMaxQty() {
        return maxQty;
    }
    public void setMaxQty(int maxQty) {
        this.maxQty = maxQty;
    }
    public int getPricePerItem() {
        return pricePerItem;
    }
    public void setPricePerItem(int pricePerItem) {
        this.pricePerItem = pricePerItem;
    }

    @Override
    public String toString() {
        return "OrderMenuItemDTO{" +
                "menuItemID=" + menuItemID +
                ", menuItemName='" + menuItemName +
                ", menuItemAmount=" + menuItemAmount +
                ", maxQty=" + maxQty +
                ", pricePerItem=" + pricePerItem +
                '}';
    }
}