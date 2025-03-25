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

import java.util.*;

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
                ingredientDTO.getCentsCostPer()
        );
        ingredientDAO.save(ingredient);
    }

    @Override
    @Transactional
    public void updateIngredient(int ingredientId, IngredientDTO ingredientDTO) {
        Ingredient ingredient = ingredientDAO.findById(ingredientId);
        int oldCost = ingredient.getCentsCostPer();

        ingredient.setIngredientName(ingredientDTO.getIngredientName());
        ingredient.setCurrentStock(ingredientDTO.getCurrentStock());
        ingredient.setCentsCostPer(ingredientDTO.getCentsCostPer());

        ingredientDAO.update(ingredient);

        if (oldCost != ingredient.getCentsCostPer()) {
            updateSingleIngredientInMenuItems(ingredientId, oldCost);
        }
    }

    // check if ingredient has associated menu items before deletion
    @Override
    @Transactional
    public List<MenuItemIngredient> deleteIngredient(int id) {
        List<MenuItemIngredient> assocMenuItems = menuItemIngredientDAO.findAllByIngredientId(id);
        if (assocMenuItems.isEmpty()) {
            ingredientDAO.deleteById(id);
        }
        return assocMenuItems;
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
    @Transactional
    public void saveMenuItem(MenuItemDTO menuItemDTO) {
        MenuItem menuItem = new MenuItem(
                menuItemDTO.getDishName(),
                menuItemDTO.getDescription(),
                menuItemDTO.getMenuCategory(),
                menuItemDTO.getIsAvailable()
        );

        List<int[]> newIngredientQuantityArray = menuItemDTO.getIngredientIdAmounts();

        mapIngredientsToMenuItem(menuItem, newIngredientQuantityArray);

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

        List<int[]> newIngredientQuantityArray = menuItemDTO.getIngredientIdAmounts();

        mapIngredientsToMenuItem(menuItem, newIngredientQuantityArray);

        menuItemDAO.update(menuItem);
    }

    @Override
    @Transactional
    public void deleteMenuItem(int menuItemId) {
        menuItemIngredientDAO.deleteByMenuItemId(menuItemId);
        menuItemDAO.deleteById(menuItemId);
    }

    private void mapIngredientsToMenuItem(MenuItem menuItem, List<int[]> ingredientsQuantities) {
        for (int[] ingredientIdQty : ingredientsQuantities) {
            int ingredientId = ingredientIdQty[0];
            int quantity = ingredientIdQty[1];
            Ingredient ingredient = ingredientDAO.findById(ingredientId);
            MenuItemIngredient menuItemIngredient = new MenuItemIngredient(menuItem, ingredient, quantity);
            menuItem.addIngredient(menuItemIngredient);
            int cost = ingredient.getCentsCostPer() * quantity;
            menuItem.setPriceCents(menuItem.getPriceCents() + cost);
        }
    }

    //updates cost of menuItem if used ingredient cost has been changed
    @Transactional
    protected void updateSingleIngredientInMenuItems(int ingredientId, int oldCost) {
        List<MenuItemIngredient> menuItemIngredients = menuItemIngredientDAO.findAllByIngredientId(ingredientId);
        Ingredient ingredient = menuItemIngredients.getFirst().getIngredient();

        for (MenuItemIngredient menuItemIngredient : menuItemIngredients) {
            MenuItem currentMenuItem = menuItemIngredient.getMenuItem();
            menuItemIngredientDAO.deleteByMenuItemIdIngredientId(currentMenuItem.getId(), ingredientId);

            List<MenuItemIngredient> currentMenuItemIngredients = currentMenuItem.getMenuItemIngredients();
            currentMenuItemIngredients.remove(menuItemIngredient);

            MenuItemIngredient newMenuItemIngredient = new MenuItemIngredient(currentMenuItem, ingredient, menuItemIngredient.getQuantityUsed());
            currentMenuItemIngredients.add(newMenuItemIngredient);

            int newCost = ( currentMenuItem.getPriceCents() - (oldCost * menuItemIngredient.getQuantityUsed()) + (ingredient.getCentsCostPer() * menuItemIngredient.getQuantityUsed()) );
            currentMenuItem.setPriceCents(newCost);

            menuItemDAO.update(currentMenuItem);

        }
    }

    @Override
    public List<String[]> buildRecipeByMenuItem(MenuItem menuItem) {
        List<String[]> menuItemRecipe = new ArrayList<>();
        List<MenuItemIngredient> ingredients = menuItem.getMenuItemIngredients();
        for (MenuItemIngredient menuItemIngredient : ingredients) {
            String menuItemQuantityWithUnit = menuItemIngredient.getQuantityUsed() + " " + menuItemIngredient.getIngredient().getUnitOfMeasure();
            menuItemRecipe.add(new String[]{menuItemIngredient.getIngredient().getIngredientName(), menuItemQuantityWithUnit});
        }
        return menuItemRecipe;
    }

    @Override
    public List<int[]> buildIngredientIdAmounts(Integer[] ingredientIdsAmountsKeys, Integer[] ingredientIdAmountValues) {
        List<int[]> ingredientIdsQty = new ArrayList<>();
        for(int i = 0; i < ingredientIdsAmountsKeys.length; i++){
            ingredientIdsQty.add(new int[]{ingredientIdsAmountsKeys[i], ingredientIdAmountValues[i]});
        }
        return ingredientIdsQty;
    }
}