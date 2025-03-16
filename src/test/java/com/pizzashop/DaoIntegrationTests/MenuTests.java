package com.pizzashop.DaoIntegrationTests;

import com.pizzashop.config.SecConfig;
import com.pizzashop.controllers.RegistrationController;
import com.pizzashop.dao.IngredientDAO;
import com.pizzashop.dao.MenuItemDAO;
import com.pizzashop.entities.*;
import com.pizzashop.services.MenuItemService;
import com.pizzashop.services.UserRegistrationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest // uses H2 in memory db instead of main db IF on classpath
// needed because not within main com.pizzashop package
@ComponentScan(
        basePackages = "com.pizzashop",
        includeFilters = @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = {
                MenuItemDAO.class,
                IngredientDAO.class,
                MenuItemService.class,
        }),
        excludeFilters = @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = {
                UserRegistrationService.class,
                RegistrationController.class,
                SecConfig.class
        })
)
public class MenuTests {

    @Autowired
    private MenuItemService menuItemService;
    @Autowired
    private MenuItemDAO menuItemDAO;
    @Autowired
    private IngredientDAO ingredientDAO;

    private Map<String, Integer> sodaSizes = new HashMap<>();

    @BeforeEach
    void setUp() {
        sodaSizes.put("Lg", 32);
        sodaSizes.put("Md", 24);
        sodaSizes.put("Sm", 16);


//        order = new Order(user, LocalDateTime.now());

        // Creating dishes and quantities are done once upon dish creation.

        //create breadsticks dish and it's ingredients
        MenuItem breadSticks = new MenuItem("Bread sticks", "sticks of bread");
        menuItemDAO.save(breadSticks);
        Ingredient dough = new Ingredient("Dough", 50, "pounds", 100);
        ingredientDAO.save(dough);
        // set ingredient quantities
        Map<String, Integer> breadSticksIngredientsQuantities = new HashMap<>();
        breadSticksIngredientsQuantities.put(dough.getIngredientName(), 1);
        menuItemService.mapIngredientsToMenuItem(breadSticks.getDishName(), breadSticksIngredientsQuantities);

        //create spaghetti dish and it's ingredients
        MenuItem spaghetti = new MenuItem("Spaghetti Bolognese", "Pasta with a meat and tomato sauce");
        menuItemDAO.save(spaghetti);
        Ingredient tomatoSauce = new Ingredient("Tomato sauce", 2000, "cups", 25);
        ingredientDAO.save(tomatoSauce);
        Ingredient pasta = new Ingredient("Pasta", 100, "pounds", 200);
        ingredientDAO.save(pasta);
        Ingredient groundBeef = new Ingredient("Ground Beef", 50, "pounds", 350);
        ingredientDAO.save(groundBeef);
        // set ingredient quantities
        Map<String, Integer> spaghettiIngredientsQuantities = new HashMap<>();
        spaghettiIngredientsQuantities.put(tomatoSauce.getIngredientName(), 2);
        spaghettiIngredientsQuantities.put(pasta.getIngredientName(), 1);
        spaghettiIngredientsQuantities.put(groundBeef.getIngredientName(), 1);
        menuItemService.mapIngredientsToMenuItem(spaghetti.getDishName(), spaghettiIngredientsQuantities);

        //create soda and it's ingredients
        MenuItem lgSoda = new MenuItem("Lg Soda", "Large Soda");
        menuItemDAO.save(lgSoda);
        Ingredient soda = new Ingredient("Soda", 1280, "oz", 10);
        ingredientDAO.save(soda);
        // set ingredient quantities
        Map<String, Integer> sodaIngredientsQuantities = new HashMap<>();
        sodaIngredientsQuantities.put(soda.getIngredientName(), sodaSizes.get("Lg"));
        menuItemService.mapIngredientsToMenuItem(lgSoda.getDishName(), sodaIngredientsQuantities);
    }

    @Test
    public void openMenuTest() {
        List<MenuItem> menuItems = menuItemDAO.findAll();

        // look at menu items and mapped ingredients
        for (MenuItem menuItem : menuItems) {
            System.out.println("Menu Item Name: " + menuItem.getDishName() + "\nIngredients:");
            for (MenuItemIngredient menuItemIngredient : menuItem.getMenuItemIngredients()) {
                Ingredient ingredient = menuItemIngredient.getIngredient();
                System.out.println(ingredient.getIngredientName() + " quantity: " + menuItemIngredient.getQuantityUsed());
            }
        }

        assertEquals(3, menuItems.size());
    }
}
