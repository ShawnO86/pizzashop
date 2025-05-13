package com.pizzashop.services;

import com.pizzashop.dao.IngredientDAO;
import com.pizzashop.dao.MenuItemDAO;
import com.pizzashop.dao.MenuItemIngredientDAO;
import com.pizzashop.dao.OrderMenuItemDAO;
import com.pizzashop.dto.IngredientDTO;
import com.pizzashop.dto.MenuItemDTO;
import com.pizzashop.entities.*;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class MenuItemServiceImpl implements MenuItemService {

    private final MenuItemDAO menuItemDAO;
    private final IngredientDAO ingredientDAO;
    private final MenuItemIngredientDAO menuItemIngredientDAO;
    private final OrderMenuItemDAO orderMenuItemDAO;

    @Autowired
    public MenuItemServiceImpl(MenuItemDAO menuItemDAO, IngredientDAO ingredientDAO,
                               MenuItemIngredientDAO menuItemIngredientDAO, OrderMenuItemDAO orderMenuItemDAO) {
        this.menuItemDAO = menuItemDAO;
        this.ingredientDAO = ingredientDAO;
        this.menuItemIngredientDAO = menuItemIngredientDAO;
        this.orderMenuItemDAO = orderMenuItemDAO;
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
    public void saveIngredient(IngredientDTO ingredientDTO, Integer ingredientId) {
        Ingredient ingredient;
        if (ingredientId == null) {
            ingredient = new Ingredient(
                    ingredientDTO.getIngredientName(),
                    ingredientDTO.getCurrentStock(),
                    ingredientDTO.getCentsCostPer(),
                    ingredientDTO.getMarkupMulti()
            );
            ingredient.setIsPizzaTopping(ingredientDTO.getIsPizzaTopping());
            ingredientDAO.save(ingredient);
        } else {
            ingredient = ingredientDAO.findById(ingredientId);
            int oldCost = ingredient.getCentsCostPer();
            int oldStock = ingredient.getCurrentStock();
            int oldMulti = ingredient.getMarkupMulti();
            ingredient.setIngredientName(ingredientDTO.getIngredientName());
            ingredient.setCurrentStock(ingredientDTO.getCurrentStock());
            ingredient.setCentsCostPer(ingredientDTO.getCentsCostPer());
            ingredient.setMarkupMulti(ingredientDTO.getMarkupMulti());
            ingredient.setIsPizzaTopping(ingredientDTO.getIsPizzaTopping());
            ingredientDAO.update(ingredient);

            if (oldCost != ingredientDTO.getCentsCostPer() || oldMulti != ingredientDTO.getMarkupMulti()) {
                updateAllMenuItemsCostPriceByIngredient(ingredient, oldCost);
            }
            if (oldStock != ingredientDTO.getCurrentStock()) {
                updateAllMenuItemsAmountAvailableByIngredient(ingredient);
            }
        }
    }

    // check if ingredient has associated menu items before deletion
    @Override
    @Transactional
    public List<MenuItemIngredient> deleteIngredient(int ingredientId) {
        List<MenuItemIngredient> assocMenuItems = menuItemIngredientDAO.findAllByIngredientId(ingredientId);
        if (assocMenuItems.isEmpty()) {
            ingredientDAO.deleteById(ingredientId);
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

        mapIngredientsToMenuItem(menuItem, menuItemDTO.getIngredientIdAmounts());

        menuItem.setMarkupMultiplier(menuItemDTO.getMarkupMultiplier());
        setSingleMenuItemCostPrice(menuItem);

        menuItem.setAmountAvailable(updateMenuItemAmountAvailable(menuItem));

        menuItemDAO.save(menuItem);
    }

    @Override
    @Transactional
    public void updateMenuItem(int menuItemId, MenuItemDTO menuItemDTO) {
        MenuItem menuItem = menuItemDAO.findById(menuItemId);
        if (!menuItem.getDishName().equals(PizzaSizeEnum.SMALL.getPizzaName()) &&
                !menuItem.getDishName().equals(PizzaSizeEnum.MEDIUM.getPizzaName()) &&
                !menuItem.getDishName().equals(PizzaSizeEnum.LARGE.getPizzaName())) {

            menuItem.setDishName(menuItemDTO.getDishName());
            menuItem.setMenuCategory(menuItemDTO.getMenuCategory());
        }

        menuItemIngredientDAO.deleteByMenuItemId(menuItemId);
        menuItem.setMarkupMultiplier(menuItemDTO.getMarkupMultiplier());
        menuItem.setCostCents(0);
        menuItem.setDescription(menuItemDTO.getDescription());
        menuItem.setIsAvailable(menuItemDTO.getIsAvailable());

        mapIngredientsToMenuItem(menuItem, menuItemDTO.getIngredientIdAmounts());

        setSingleMenuItemCostPrice(menuItem);
        menuItem.setAmountAvailable(updateMenuItemAmountAvailable(menuItem));

        menuItemDAO.update(menuItem);
    }

    @Override
    @Transactional
    public List<OrderMenuItem> deleteMenuItem(int menuItemId) {
        List<OrderMenuItem> associatedOrders = orderMenuItemDAO.findAllByMenuItemId(menuItemId);
        if (associatedOrders.isEmpty()) {
            menuItemIngredientDAO.deleteByMenuItemId(menuItemId);
            menuItemDAO.deleteById(menuItemId);
        }
        return associatedOrders;
    }

    private void mapIngredientsToMenuItem(MenuItem menuItem, List<int[]> ingredientsQuantities) {
        int cost = 0;
        int price = 0;
        for (int[] ingredientIdQty : ingredientsQuantities) {
            int ingredientId = ingredientIdQty[0];
            int quantity = ingredientIdQty[1];
            Ingredient ingredient = ingredientDAO.findById(ingredientId);
            MenuItemIngredient menuItemIngredient = new MenuItemIngredient(menuItem, ingredient, quantity);
            menuItem.addIngredient(menuItemIngredient);
            cost += ingredient.getCentsCostPer() * quantity;
            price += ingredient.getCentsPricePer() * quantity;
        }
        menuItem.setCostCents(cost);
        menuItem.setPriceCents(price);
    }

    //updates cost of menuItem if used ingredient cost has been changed and amount available if stock changed.
    @Transactional
    protected void updateAllMenuItemsCostPriceByIngredient(Ingredient ingredient, int oldCost) {
        List<MenuItemIngredient> menuItemIngredients = ingredient.getMenuItemIngredients();

        for (MenuItemIngredient menuItemIngredient : menuItemIngredients) {
            MenuItem currentMenuItem = menuItemIngredient.getMenuItem();

            int newCost = ( currentMenuItem.getCostCents() - ( oldCost * menuItemIngredient.getQuantityUsed() ) + ( ingredient.getCentsCostPer() * menuItemIngredient.getQuantityUsed() ) );

            currentMenuItem.setCostCents(newCost);
            currentMenuItem.setPriceCents(newCost * currentMenuItem.getMarkupMultiplier());

            menuItemDAO.update(currentMenuItem);
        }
    }

    @Transactional
    protected void setSingleMenuItemCostPrice(MenuItem menuItem) {
        List<MenuItemIngredient> menuItemIngredients = menuItem.getMenuItemIngredients();
        int cost = 0;

        for (MenuItemIngredient menuItemIngredient : menuItemIngredients) {
            Ingredient currentIngredient = menuItemIngredient.getIngredient();
            cost += currentIngredient.getCentsCostPer() * menuItemIngredient.getQuantityUsed();
        }
        menuItem.setCostCents(cost);
        menuItem.setPriceCents(cost * menuItem.getMarkupMultiplier());
    }

    @Override
    @Transactional
    public void updateAllMenuItemsAmountAvailableByIngredient(Ingredient ingredient) {
        List<MenuItemIngredient> menuItemIngredients = ingredient.getMenuItemIngredients();

        for (MenuItemIngredient menuItemIngredient : menuItemIngredients) {
            MenuItem currentMenuItem = menuItemIngredient.getMenuItem();
            System.out.println(currentMenuItem);
            currentMenuItem.setAmountAvailable(updateMenuItemAmountAvailable(currentMenuItem));

            menuItemDAO.update(currentMenuItem);
        }
    }

    @Override
    public List<String[]> buildRecipeByMenuItem(MenuItem menuItem) {
        List<String[]> menuItemRecipe = new ArrayList<>();
        List<MenuItemIngredient> ingredients = menuItem.getMenuItemIngredients();
        for (MenuItemIngredient menuItemIngredient : ingredients) {
            menuItemRecipe.add(new String[]{
                    menuItemIngredient.getIngredient().getIngredientName(),
                    String.valueOf(menuItemIngredient.getQuantityUsed()),
                    menuItemIngredient.getIngredient().getUnitOfMeasure()
            });

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

    @Override
    public int updateMenuItemAmountAvailable(MenuItem menuItem) {
        int lowestInventoryAvailable = Integer.MAX_VALUE;
        List<MenuItemIngredient> menuItemIngredients = menuItem.getMenuItemIngredients();

        for (MenuItemIngredient menuItemIngredient : menuItemIngredients) {
            Ingredient currentIngredient = menuItemIngredient.getIngredient();

            int futureStock = currentIngredient.getCurrentStock() / menuItemIngredient.getQuantityUsed();

            // setting the lowest amount of inventory available for this ingredient
            if (futureStock < lowestInventoryAvailable) {
                lowestInventoryAvailable = futureStock;
            }
        }

        return lowestInventoryAvailable;
    }

    @Override
    public int updateMenuItemAmountAvailableWithIngredients(MenuItem menuItem, List<MenuItemIngredient> ingredients) {
        int lowestInventoryAvailable = Integer.MAX_VALUE;

        for (MenuItemIngredient menuItemIngredient : ingredients) {
            Ingredient currentIngredient = menuItemIngredient.getIngredient();

            int futureStock = currentIngredient.getCurrentStock() / menuItemIngredient.getQuantityUsed();

            // setting the lowest amount of inventory available for this ingredient
            if (futureStock < lowestInventoryAvailable) {
                lowestInventoryAvailable = futureStock;
            }
        }

        return lowestInventoryAvailable;
    }

}