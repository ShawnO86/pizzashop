package com.pizzashop.DaoIntegrationTests;

import com.pizzashop.config.SecConfig;
import com.pizzashop.controllers.ManagementController;
import com.pizzashop.controllers.RegistrationController;
import com.pizzashop.dao.IngredientDAO;
import com.pizzashop.dao.MenuItemDAO;
import com.pizzashop.dao.UserDAO;
import com.pizzashop.dto.MenuItemDTO;
import com.pizzashop.entities.*;
import com.pizzashop.services.MenuItemService;
import com.pizzashop.services.OrderService;
import com.pizzashop.services.UserRegistrationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest // uses H2 in memory db instead of main db IF on classpath
// needed because not within main com.pizzashop package
@ComponentScan(
        basePackages = "com.pizzashop",
        includeFilters = @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = {
                MenuItemDAO.class,
                IngredientDAO.class,
                MenuItemService.class,
                OrderService.class
        }),
        excludeFilters = @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = {
                UserRegistrationService.class,
                RegistrationController.class,
                SecConfig.class,
                ManagementController.class
        })
)
public class MenuTests {

    @Autowired
    private MenuItemService menuItemService;
    @Autowired
    private MenuItemDAO menuItemDAO;
    @Autowired
    private IngredientDAO ingredientDAO;
    @Autowired
    private OrderService orderService;
    @Autowired
    private UserDAO userDAO;


    private static final Map<String, Integer> sodaSizes = new HashMap<>();
    static {
        sodaSizes.put("Lg", 32);
        sodaSizes.put("Md", 24);
        sodaSizes.put("Sm", 16);
    }

    @BeforeEach
    void setUp() {
        // Creating dishes and quantities are done once upon dish creation outside of testing.

        //create breadsticks dish and it's ingredients
        MenuItemDTO breadSticks = new MenuItemDTO("Bread sticks", "sticks of bread", MenuCategoryEnum.APP);
        Ingredient dough = new Ingredient("Dough", 50, 100);
        System.out.println("inserting test ingredients...");
        ingredientDAO.save(dough);
        // set ingredient quantities and save menuItem
        List<int[]> breadSticksIngredientsQuantities = new ArrayList<>();
        breadSticksIngredientsQuantities.add(new int[]{dough.getId(), 1});
        breadSticks.setIngredientIdAmounts(breadSticksIngredientsQuantities);
        breadSticks.setIsAvailable(true);
        System.out.println("inserting test breadsticks...");
        menuItemService.saveMenuItem(breadSticks);

        //create spaghetti dish and it's ingredients
        MenuItemDTO spaghetti = new MenuItemDTO("Spaghetti Bolognese", "Pasta with a meat and tomato sauce", MenuCategoryEnum.PASTA);
        Ingredient tomatoSauce = new Ingredient("Tomato sauce", 2000, 50);
        System.out.println("inserting test ingredients...");
        ingredientDAO.save(tomatoSauce);
        Ingredient pasta = new Ingredient("Pasta", 100, 50);
        ingredientDAO.save(pasta);
        Ingredient groundBeef = new Ingredient("Ground Beef", 50,300);
        ingredientDAO.save(groundBeef);
        // set ingredient quantities
        List<int[]> spaghettiIngredientsQuantities = new ArrayList<>();
        spaghettiIngredientsQuantities.add(new int[]{tomatoSauce.getId(), 2});
        spaghettiIngredientsQuantities.add(new int[]{pasta.getId(), 1});
        spaghettiIngredientsQuantities.add(new int[]{groundBeef.getId(), 1});
        spaghetti.setIngredientIdAmounts(spaghettiIngredientsQuantities);
        spaghetti.setIsAvailable(true);

        System.out.println("inserting test spaghetti...");
        menuItemService.saveMenuItem(spaghetti);

        //create soda and it's ingredients
        MenuItemDTO lgSoda = new MenuItemDTO("Lg Soda", "Large Soda", MenuCategoryEnum.DRINK);
        Ingredient soda = new Ingredient("Soda", 1280, 3);
        System.out.println("inserting test ingredients...");
        ingredientDAO.save(soda);
        // set ingredient quantities
        List<int[]> sodaIngredientsQuantities = new ArrayList<>();
        sodaIngredientsQuantities.add(new int[]{soda.getId(), sodaSizes.get("Lg")});
        lgSoda.setIngredientIdAmounts(sodaIngredientsQuantities);
        lgSoda.setIsAvailable(true);

        System.out.println("inserting test soda...");
        menuItemService.saveMenuItem(lgSoda);
    }

    @Test
    public void openMenuTest() {
        System.out.println("finding test menu...");
        List<MenuItem> menuItems = menuItemDAO.findAll();

        // look at menu items and mapped ingredients
        for (MenuItem menuItem : menuItems) {
            System.out.println("Menu Item Name: " + menuItem.getDishName());
            System.out.println("Category: " + menuItem.getMenuCategory() +
                    "\ncost: " + menuItem.getCostCents() +
                    "\nprice:" + menuItem.getPriceCents() +
                    "\nAmount Available: " + menuItem.getAmountAvailable() +
                    "\nIngredients:");
            for (MenuItemIngredient menuItemIngredient : menuItem.getMenuItemIngredients()) {
                Ingredient ingredient = menuItemIngredient.getIngredient();
                System.out.println(ingredient.getIngredientName() + " -- quantity: " + menuItemIngredient.getQuantityUsed());
            }
        }

        assertEquals(3, menuItems.size());
    }

    @Test
    public void submitOrderForFulfillmentTest() {
        User user;
        UserDetail userDetail;
        Role role;
        user = new User();
        user.setUsername("TestName");
        user.setPassword("TestPassword");
        user.setActive(true);

        userDetail = new UserDetail();
        userDetail.setFirstName("TestFirstName");
        userDetail.setLastName("TestLastName");
        userDetail.setEmail("TestEmail");
        userDetail.setPhone("TestPhone");
        userDetail.setAddress("TestAddress");
        userDetail.setCity("TestCity");
        userDetail.setState("TestState");
        user.setUserDetail(userDetail);

        role = new Role(RoleEnum.ROLE_CUSTOMER);
        user.addRole(role);
        userDAO.save(user);

        List<Integer> menuItemsIds = new ArrayList<>(List.of(1, 2, 3));
        String[] menuItemsNames = new String[]{"Bread sticks", "Spaghetti Bolognese", "Lg Soda"};

        List<MenuItem> menuItemsBefore = menuItemDAO.findAll();

        System.out.println("menuItems BEFORE ---> " + menuItemsBefore);

        List<List<String>> orderResponse = orderService.submitOrderForFulfillment(menuItemsIds, menuItemsNames, new int[]{1, 1, 1}, "TestName");

        System.out.println(orderResponse);

        List<MenuItem> menuItemsAfter = menuItemDAO.findAll();

        System.out.println("menuItems AFTER ---> " + menuItemsAfter);

        assertEquals(3, orderResponse.size());
        assertEquals("Success!", orderResponse.getFirst().getFirst());
    }

}
