package com.pizzashop.services;

import com.pizzashop.dto.CustomPizzaDTO;
import com.pizzashop.dto.OrderDTO;

import java.util.List;
import java.util.Map;

public interface OrderService {

    int submitOrder(OrderDTO order, String username);

    Map<String, List<String>> submitOrderForValidation(OrderDTO order);
}
