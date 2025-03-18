package com.pizzashop.services;

import com.pizzashop.dao.IngredientDAO;
import com.pizzashop.dao.MenuItemDAO;
import com.pizzashop.dto.IngredientDTO;
import com.pizzashop.entities.Ingredient;
import com.pizzashop.entities.MenuItem;
import com.pizzashop.entities.MenuItemIngredient;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
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
    public Ingredient findIngredientByName(String name) {
        return ingredientDAO.findByName(name);
    }

    @Override
    public Ingredient findIngredientById(int id) {
        return ingredientDAO.findById(id);
    }

    @Override
    public List<Ingredient> findAllIngredients() {
        return ingredientDAO.findAll();
    }

    @Override
    @Transactional
    public void saveIngredient(IngredientDTO ingredientDTO) {
        Ingredient ingredient = new Ingredient(
                ingredientDTO.getIngredientName(),
                ingredientDTO.getCurrentStock(),
                ingredientDTO.getUnitOfMeasure(),
                ingredientDTO.getCentsCostPer()
        );
        ingredientDAO.save(ingredient);
    }

    @Override
    @Transactional
    public void updateIngredient(int ingredientId, IngredientDTO ingredientDTO) {
        System.out.println("in updateIngredient" + ingredientId + "\n" + ingredientDTO);

        Ingredient ingredient = ingredientDAO.findById(ingredientId);

        ingredient.setIngredientName(ingredientDTO.getIngredientName());
        ingredient.setCurrentStock(ingredientDTO.getCurrentStock());
        ingredient.setUnitOfMeasure(ingredientDTO.getUnitOfMeasure());
        ingredient.setCentsCostPer(ingredientDTO.getCentsCostPer());

        ingredientDAO.update(ingredient);
    }

    @Override
    @Transactional
    public void deleteIngredient(int id) {
        ingredientDAO.deleteById(id);
    }


    // set this in the menu creation form
    @Override
    @Transactional
    public void mapIngredientsToMenuItem(MenuItem menuItem, Map<String, Integer> ingredientsQuantities) {
        ingredientsQuantities.forEach((ingredientName, quantityUsed) -> {
            Ingredient ingredient = ingredientDAO.findByName(ingredientName);
            // set join table record
            MenuItemIngredient menuItemIngredient = new MenuItemIngredient(menuItem, ingredient, quantityUsed);
            // map to ingredient table
            menuItem.addIngredient(menuItemIngredient);
            // calculate price
            int cost = ingredient.getCentsCostPer() * quantityUsed;
            // add to the running total
            menuItem.setPriceCents(menuItem.getPriceCents() + cost);
        });
        // save menuItem with mapped ingredient and final price
        menuItemDAO.save(menuItem);
    }

}