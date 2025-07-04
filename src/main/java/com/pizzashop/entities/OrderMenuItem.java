package com.pizzashop.entities;

import jakarta.persistence.*;

@Entity
@Table(name = "orders_menuitems")
public class OrderMenuItem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "order_id")
    private Order order;

    @ManyToOne
    @JoinColumn(name = "menu_item_id")
    private MenuItem menuItem;

    @ManyToOne
    @JoinColumn(name = "custom_pizza_id")
    private CustomPizza customPizza;

    @Column(name = "item_quantity")
    private int itemQuantity;

    public OrderMenuItem() {}

    public OrderMenuItem(Order order) {
        this.order = order;
    }

    public int getId() {
        return id;
    }

    public Order getOrder() {
        return order;
    }

    public MenuItem getMenuItem() {
        return menuItem;
    }

    public void setMenuItem(MenuItem menuItem) {
        this.menuItem = menuItem;
    }

    public CustomPizza getCustomPizza() {
        return customPizza;
    }

    public void setCustomPizza(CustomPizza customPizza) {
        this.customPizza = customPizza;
    }

    public int getItemQuantity() {
        return itemQuantity;
    }

    public void setItemQuantity(int itemQuantity) {
        this.itemQuantity = itemQuantity;
    }

    @Override
    public String toString() {
        return "OrderMenuItem{" +
                "menuItem=" + menuItem +
                ", customPizza=" + customPizza +
                ", itemQuantity=" + itemQuantity +
                '}';
    }
}
