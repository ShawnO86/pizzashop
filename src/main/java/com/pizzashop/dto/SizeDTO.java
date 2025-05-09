package com.pizzashop.dto;

import com.pizzashop.entities.PizzaSizeEnum;

public class SizeDTO {
    private PizzaSizeEnum size;
    private int price;

    public SizeDTO() {}

    public SizeDTO(PizzaSizeEnum size, int price) {
        this.size = size;
        this.price = price;
    }

    public PizzaSizeEnum getSize() {
        return size;
    }
    public void setSize(PizzaSizeEnum size) {
        this.size = size;
    }
    public int getPrice() {
        return price;
    }
    public void setPrice(int price) {
        this.price = price;
    }

    @Override
    public String toString() {
        return "SizeDTO{" +
                "size=" + size +
                ", price=" + price +
                '}';
    }
}
