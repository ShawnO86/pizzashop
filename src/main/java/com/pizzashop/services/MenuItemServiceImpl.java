package com.pizzashop.services;

import com.pizzashop.dao.IngredientDAO;
import com.pizzashop.dao.MenuItemDAO;
import com.pizzashop.dao.MenuItemIngredientDAO;
import com.pizzashop.dto.IngredientDTO;
import com.pizzashop.dto.MenuItemDTO;
import com.pizzashop.entities.Ingredient;
import com.pizzashop.entities.MenuItem;
import com.pizzashop.entities.MenuItemIngredient;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class MenuItemServiceImpl implements MenuItemService {

    MenuItemDAO menuItemDAO;
    IngredientDAO ingredientDAO;
    MenuItemIngredientDAO menuItemIngredientDAO;

    @Autowired
    public MenuItemServiceImpl(MenuItemDAO menuItemDAO, IngredientDAO ingredientDAO, MenuItemIngredientDAO menuItemIngredientDAO) {
        this.menuItemDAO = menuItemDAO;
        this.ingredientDAO = ingredientDAO;
        this.menuItemIngredientDAO = menuItemIngredientDAO;
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

    @Override
    public List<MenuItem> findAllMenuItems() {
        return menuItemDAO.findAll();
    }

    @Override
    public MenuItem findMenuItemByName(String name) {
        return menuItemDAO.findByName(name);
    }

    @Override
    public MenuItem findMenuItemById(int id) {
        return menuItemDAO.findById(id);
    }

    @Override
    public Map<String, String> findMenuItemRecipeByMenuId(int menuItemId) {
        List<MenuItemIngredient> menuItemIngredients = menuItemIngredientDAO.findAllByMenuItemId(menuItemId);
        Map<String, String> menuItemIngredientQuantityMap = new HashMap<String, String>();


        for (MenuItemIngredient menuItemIngredient : menuItemIngredients) {
            String menuItemQuantityWithUnit = menuItemIngredient.getQuantityUsed() + " " + menuItemIngredient.getIngredient().getUnitOfMeasure();
            menuItemIngredientQuantityMap.put(menuItemIngredient.getIngredient().getIngredientName(), menuItemQuantityWithUnit);
        }

        return menuItemIngredientQuantityMap;
    }

    @Override
    @Transactional
    public void saveMenuItem(MenuItemDTO menuItemDTO) {
        MenuItem menuItem = new MenuItem(
                menuItemDTO.getDishName(),
                menuItemDTO.getDescription(),
                menuItemDTO.getMenuCategory(),
                menuItemDTO.getIsAvailable()
        );

        Map<Integer, Integer> ingredientIdQuantityMap = menuItemDTO.getIngredientIdAmounts();

        mapIngredientsToMenuItem(menuItem, ingredientIdQuantityMap);

        menuItemDAO.save(menuItem);
    }

    @Override
    @Transactional
    public void updateMenuItem(int menuItemId, MenuItemDTO menuItemDTO) {
        MenuItem menuItem = menuItemDAO.findById(menuItemId);
        menuItemIngredientDAO.deleteByMenuItemId(menuItemId);

        menuItem.setPriceCents(0);
        menuItem.setDishName(menuItemDTO.getDishName());
        menuItem.setDescription(menuItemDTO.getDescription());
        menuItem.setMenuCategory(menuItemDTO.getMenuCategory());

        Map<Integer, Integer> newIngredientQuantityMap = menuItemDTO.getIngredientIdAmounts();

        mapIngredientsToMenuItem(menuItem, newIngredientQuantityMap);

        menuItemDAO.update(menuItem);
    }

    @Override
    @Transactional
    public void deleteMenuItem(int menuItemId) {
        MenuItem menuItem = menuItemDAO.findById(menuItemId);

        menuItemDAO.delete(menuItem);
    }

    private void mapIngredientsToMenuItem(MenuItem menuItem, Map<Integer, Integer> ingredientsQuantities) {
        ingredientsQuantities.forEach((ingredientId,quantity) -> {
            Ingredient ingredient = ingredientDAO.findById(ingredientId);
            MenuItemIngredient menuItemIngredient = new MenuItemIngredient(menuItem, ingredient, quantity);
            menuItem.addIngredient(menuItemIngredient);
            int cost = ingredient.getCentsCostPer() * quantity;
            menuItem.setPriceCents(menuItem.getPriceCents() + cost);
        });
        //adds price markup to total cost of ingredients
        menuItem.setPriceCents(menuItem.getPriceCents() * 3);
    }

}