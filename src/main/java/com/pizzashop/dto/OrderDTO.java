package com.pizzashop.dto;

import java.util.List;

public class OrderDTO {
    private List<OrderMenuItemDTO> menuItemDTOList;
    private List<CustomPizzaDTO> customPizzaDTOList;

    public OrderDTO() {}

    public List<OrderMenuItemDTO> getMenuItemDTOList() {
        return menuItemDTOList;
    }
    public void setMenuItemDTOList(List<OrderMenuItemDTO> menuItemDTOList) {
        this.menuItemDTOList = menuItemDTOList;
    }
    public List<CustomPizzaDTO> getCustomPizzaDTOList() {
        return customPizzaDTOList;
    }
    public void setCustomPizzaDTOList(List<CustomPizzaDTO> customPizzaDTOList) {
        this.customPizzaDTOList = customPizzaDTOList;
    }
}
