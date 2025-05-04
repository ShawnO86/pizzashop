package com.pizzashop.services;

import com.pizzashop.dto.OrderDTO;

import java.util.List;

public interface OrderService {

    List<String> submitOrderForValidation(OrderDTO order);
}
