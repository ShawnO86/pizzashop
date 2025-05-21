package com.pizzashop.services;

import com.pizzashop.dto.OrderDTO;
import com.pizzashop.entities.Order;

import java.util.List;
import java.util.Map;

public interface OrderService {

    Order submitOrder(OrderDTO order, String username);

    Map<String, List<String>> submitOrderForValidation(OrderDTO order);

    OrderDTO convertOrderToDTO(Order order, boolean needUser);
}
