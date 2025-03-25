package com.pizzashop.entities;

import jakarta.persistence.*;

@Entity
@Table(name = "orders_menuitems")
public class OrderMenuItem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private int id;

    @ManyToOne
    @JoinColumn(name = "order_id")
    private Order order;

    @ManyToOne
    @JoinColumn(name = "menu_item_id")
    private MenuItem menuItem;

    @Column(name = "item_quantity")
    private int itemQuantity;

    public OrderMenuItem() {}

    public OrderMenuItem(Order order, MenuItem menuItem, int itemQuantity) {
        this.order = order;
        this.menuItem = menuItem;
        this.itemQuantity = itemQuantity;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Order getOrder() {
        return order;
    }

    public void setOrder(Order order) {
        this.order = order;
    }

    public MenuItem getMenuItem() {
        return menuItem;
    }

    public void setMenuItem(MenuItem menuItem) {
        this.menuItem = menuItem;
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
                "order=" + order +
                ", menuItem=" + menuItem +
                ", itemQuantity=" + itemQuantity +
                '}';
    }
}
