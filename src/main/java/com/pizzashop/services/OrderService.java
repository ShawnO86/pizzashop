package com.pizzashop.services;

import com.pizzashop.dto.OrderDTO;
import com.pizzashop.entities.MenuItem;
import com.pizzashop.entities.Order;
import com.pizzashop.entities.User;

import java.util.List;

public interface OrderService {

    void addOrderToDB(OrderDTO orderDTO, String username);
}
