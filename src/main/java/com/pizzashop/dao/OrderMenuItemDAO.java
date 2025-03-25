package com.pizzashop.dao;

import com.pizzashop.entities.OrderMenuItem;

import java.util.List;

public interface OrderMenuItemDAO {
    List<OrderMenuItem> findAll();

    List<OrderMenuItem> findAllByMenuItemId(Integer menuItemId);

    List<OrderMenuItem> findAllByOrderId(Integer orderId);

    OrderMenuItem findById(Integer id);
}
