package com.pizzashop.services;

import com.pizzashop.dto.OrderDTO;

public interface OrderService {

    void submitOrderForFulfillment(OrderDTO orderDTO, String username);
}
