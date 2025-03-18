package com.pizzashop.services;

import com.pizzashop.dao.IngredientDAO;
import com.pizzashop.dao.MenuItemDAO;
import com.pizzashop.dao.OrderDAO;
import com.pizzashop.dao.UserDAO;
import com.pizzashop.dto.OrderDTO;
import com.pizzashop.entities.MenuItem;
import com.pizzashop.entities.Order;
import com.pizzashop.entities.MenuItemIngredient;
import com.pizzashop.entities.User;
import com.pizzashop.entities.Ingredient;

import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class OrderServiceImpl implements OrderService {

    // may need to grab user from session before testing on web ???

    IngredientDAO ingredientDAO;
    MenuItemDAO menuItemDAO;
    OrderDAO orderDAO;
    UserDAO userDAO;

    @Autowired
    public OrderServiceImpl(IngredientDAO ingredientDAO, MenuItemDAO menuItemDAO, OrderDAO orderDAO, UserDAO userDAO) {
        this.ingredientDAO = ingredientDAO;
        this.menuItemDAO = menuItemDAO;
        this.orderDAO = orderDAO;
        this.userDAO = userDAO;
    }

    @Override
    @Transactional
    public void addOrderToDB(OrderDTO orderDTO, String username) {
        User user = userDAO.findByUsername(username);

        Order newOrder = new Order(user, LocalDateTime.now());
        List<MenuItem> menuItems = orderDTO.getMenuItems();
        int finalPrice = calculateTotalPrice(menuItems);

        newOrder.setMenuItems(menuItems);
        newOrder.setFinal_price_cents(finalPrice);

        orderDAO.save(newOrder);
        updateIngredientQuantities(menuItems);
    }

    @Transactional
    public void updateIngredientQuantities(List<MenuItem> menuItems) {
        for (MenuItem menuItem : menuItems) {
            List<MenuItemIngredient> ingredients = menuItem.getMenuItemIngredients();
            for (MenuItemIngredient menuItemIngredient : ingredients) {
                int currentStock = menuItemIngredient.getIngredient().getCurrentStock();
                int used = menuItemIngredient.getQuantityUsed();
                Ingredient currentIngredient = menuItemIngredient.getIngredient();
                currentIngredient.setCurrentStock(currentStock - used);
                ingredientDAO.update(currentIngredient);
            }
        }
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
