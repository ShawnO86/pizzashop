package com.pizzashop.services;

import com.pizzashop.dao.MenuItemDAO;
import com.pizzashop.dao.OrderDAO;
import com.pizzashop.dto.OrderDTO;
import com.pizzashop.entities.MenuItem;
import com.pizzashop.entities.Order;
import com.pizzashop.entities.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class OrderServiceImpl implements OrderService {

    // may need to grab user from session before testing on web ???

    MenuItemDAO menuItemDAO;
    OrderDAO orderDAO;

    @Autowired
    public OrderServiceImpl(MenuItemDAO menuItemDAO, OrderDAO orderDAO) {
        this.menuItemDAO = menuItemDAO;
        this.orderDAO = orderDAO;
    }

    // user directly added for now for testing will grab from session later ... I think
    // test adding From DTO
    @Override
    public void addOrderToDB(OrderDTO orderDTO, User user) {
        Order newOrder = new Order(user, LocalDateTime.now());
        List<MenuItem> menuItems = orderDTO.getMenuItems();
        int finalPrice = this.calculateTotalPrice(menuItems);

        newOrder.setMenuItems(menuItems);
        newOrder.setFinal_price_cents(finalPrice);

        orderDAO.save(newOrder);
    }

    // TODO - complete removeIngredients
    private void removeIngredients(List<MenuItem> menuItems) {

    }

    private int calculateTotalPrice(List<MenuItem> menuItems) {
        int totalPrice = 0;
        double priceMarkup = 0.10;

        for (MenuItem menuItem : menuItems) {
            MenuItem currentMenuItem = menuItemDAO.findByName(menuItem.getDishName());

            if (currentMenuItem != null) {
                totalPrice += currentMenuItem.getPriceCents();
            } else {
                System.out.println("Warning! No menu item found with name " + menuItem.getDishName());
            }
        }

        return (int) (totalPrice + (totalPrice * priceMarkup));
    }
}
