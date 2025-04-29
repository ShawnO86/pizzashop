package com.pizzashop.dto;

public class ToppingDTO {
    private String name;
    private Integer id;

    public ToppingDTO() {}

    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public Integer getId() {
        return id;
    }
    public void setId(Integer id) {
        this.id = id;
    }

    @Override
    public String toString() {
        return "ToppingDTO{" +
                "name='" + name + '\'' +
                ", id=" + id +
                '}';
    }
}
