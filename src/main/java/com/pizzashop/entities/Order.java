package com.pizzashop.entities;

import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "orders")
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private int id;

    @ManyToOne(cascade = {CascadeType.DETACH, CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH},
            fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @Column(name = "order_date")
    private LocalDateTime order_date;

    @Column(name = "final_price_cents")
    private int final_price_cents;

    @Column(name = "is_complete")
    private boolean is_complete;

    @Column(name = "in_progress")
    private boolean in_progress;

    @Column(name = "fulfilled_by")
    private String fulfilled_by;

    @OneToMany(mappedBy = "order" , cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private List<OrderMenuItem> orderMenuItems;

    public Order() {}

    public Order(User user, LocalDateTime order_date) {
        this.user = user;
        this.order_date = order_date;
    }

    public Order(User user, LocalDateTime order_date, List<OrderMenuItem> orderMenuItems, boolean is_complete) {
        this.user = user;
        this.order_date = order_date;
        this.orderMenuItems = orderMenuItems;
        this.is_complete = is_complete;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public LocalDateTime getOrder_date() {
        return order_date;
    }

    public void setOrder_date(LocalDateTime order_date) {
        this.order_date = order_date;
    }

    public int getFinal_price_cents() {
        return final_price_cents;
    }

    public void setFinal_price_cents(int final_price_cents) {
        this.final_price_cents = final_price_cents;
    }

    public boolean getIs_complete() {
        return is_complete;
    }

    public void setIs_complete(boolean is_complete) {
        this.is_complete = is_complete;
    }

    public boolean getIn_progress() {
        return in_progress;
    }

    public void setIn_progress(boolean in_progress) {
        this.in_progress = in_progress;
    }

    public String getFulfilled_by() {
        return fulfilled_by;
    }

    public void setFulfilled_by(String fulfilled_by) {
        this.fulfilled_by = fulfilled_by;
    }

    public List<OrderMenuItem> getOrderMenuItems() {
        return orderMenuItems;
    }

    public void setOrderMenuItems(List<OrderMenuItem> orderMenuItems) {
        this.orderMenuItems = orderMenuItems;
    }

    public void addMenuItem(OrderMenuItem orderMenuItem) {
        if (orderMenuItems == null) {
            orderMenuItems = new ArrayList<>();
        }
        this.orderMenuItems.add(orderMenuItem);
    }

    @Override
    public String toString() {
        return "Order{" +
                "id=" + id +
                ", order_date=" + order_date +
                ", final_price_cents=" + final_price_cents +
                ", menuItems=" + orderMenuItems +
                ", is_complete=" + is_complete +
                ", in_progress=" + in_progress +
                ", fulfilled_by='" + fulfilled_by + '\'' +
                '}';
    }
}
