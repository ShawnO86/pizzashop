package com.pizzashop.services;

import com.pizzashop.dao.IngredientDAO;
import com.pizzashop.dao.MenuItemDAO;
import com.pizzashop.entities.Ingredient;
import com.pizzashop.entities.MenuItem;
import com.pizzashop.entities.MenuItemIngredient;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class MenuItemServiceImpl implements MenuItemService {

    MenuItemDAO menuItemDAO;
    IngredientDAO ingredientDAO;

    @Autowired
    public MenuItemServiceImpl(MenuItemDAO menuItemDAO, IngredientDAO ingredientDAO) {
        this.menuItemDAO = menuItemDAO;
        this.ingredientDAO = ingredientDAO;
    }

    @Override
    @Transactional
    public void mapIngredientsToMenuItem(String menuItemName, Map<String, Integer> ingredientsQuantities) {
        MenuItem menuItem = menuItemDAO.findByName(menuItemName);

        ingredientsQuantities.forEach((ingredientName, quantityUsed) -> {
            Ingredient ingredient = ingredientDAO.findByName(ingredientName);
            int currentStock = ingredient.getCurrentStock();
            if (currentStock >= quantityUsed) {
                // set join table record
                MenuItemIngredient menuItemIngredient = new MenuItemIngredient(menuItem, ingredient, quantityUsed);
                // add map to ingredient table
                menuItem.addIngredient(menuItemIngredient);
                // reduce ingredient inventory
                ingredient.setCurrentStock(currentStock - quantityUsed);
                // calculate price
                int cost = ingredient.getCentsCostPer() * quantityUsed;
                int charge = (int) (cost * 0.05);
                // add to the running total
                menuItem.setPriceCents(menuItem.getPriceCents() + (cost + charge));
                // update menuItem with mapped ingredient and final price
                menuItemDAO.update(menuItem);
            } else {
                throw new IllegalArgumentException("Not enough stock of : " + ingredientName + " current stock is: " + currentStock);
            }
        });
    }
}
